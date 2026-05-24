package com.communityhub.complaint.service;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.community.repository.CommunityMemberRepository;
import com.communityhub.community.repository.CommunityRepository;
import com.communityhub.complaint.dto.ComplaintRequest;
import com.communityhub.complaint.dto.ReplyRequest;
import com.communityhub.complaint.dto.UpdateComplaintRequest;
import com.communityhub.complaint.entity.Complaint;
import com.communityhub.complaint.entity.embedded.ActionStep;
import com.communityhub.complaint.entity.embedded.Reply;
import com.communityhub.complaint.repository.ComplaintRepository;
import com.communityhub.notification.service.EmailService;
import com.communityhub.user.dto.UserResponse;
import com.communityhub.user.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintService {

    private final UserService userService;
    private final ComplaintRepository complaintRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final EmailService emailService;
    public ComplaintService(CommunityRepository communityRepository,
                            ComplaintRepository complaintRepository,
                            UserService userService, CommunityMemberRepository communityMemberRepository,EmailService emailService) {
        this.communityRepository = communityRepository;
        this.complaintRepository = complaintRepository;
        this.userService = userService;
        this.communityMemberRepository=communityMemberRepository;
        this.emailService=emailService;

    }
    public ApiResponse getComplaints(String communityId){
        List<Complaint> complaints=complaintRepository.findByCommunityId(communityId);
        if(complaints.isEmpty()){
            return ApiResponse.builder()
                    .success(true)
                    .message("No member added yet")
                    .build();
        }
        return ApiResponse.builder()
                .success(true)
                .message("complaint fetched successfully")
                .data(complaints)
                .build();

    }

    public ApiResponse createComplaint(ComplaintRequest request,
                                       String communityId,
                                       String fromUserId) {

        // ✅ Validate user exists in same community
        boolean exists = communityMemberRepository
                .existsByCommunityIdAndUserId(communityId,request.getAgainstUserId());


        if (!exists) {
            return ApiResponse.builder()
                    .success(false)
                    .message("User does not belong to this community")
                    .build();
        }

        Complaint com = Complaint.builder()
                .communityId(communityId)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .reporterLastDate(request.getReporterLastDate().toString())
                .evidenceImages(request.getEvidenceImages())
                .reportedBy(fromUserId)
                .againstUserId(request.getAgainstUserId())  // ✅ clean mapping
                .againstUserName(request.getAgainstUserName())
                .reporterUserName(request.getReporterName())
                .reporterId(request.getReporterId())
                .status("SENT")
                .createdAt(LocalDateTime.now())
                .build();

        Complaint saved = complaintRepository.save(com);
        UserResponse u=userService.getUserById(saved.getAgainstUserId());
         String mess="Complaint Created Successfully\n You filled Complaint against "+u.getFullName();
         emailService.sendMessage(u.getEmail(),mess);
        return ApiResponse.builder()
                .success(true)
                .message("Complaint created successfully")
                .data(saved)
                .build();
    }

    public ApiResponse deleteComplaint(String complaintId,String userId){

        Complaint com=complaintRepository.findById(complaintId)
                .orElseThrow(()->new RuntimeException("invalid complaint id"));
        if (!com.getReportedBy().equals(userId)){
            return ApiResponse.builder()
                    .success(false)
                    .message("You are not authorized to  delete this complaint. Only complaint creator can delete it")
                    .build();
        }
        complaintRepository.deleteById(complaintId);
        return ApiResponse.builder()
                .success(true)
                .message("Complaint deleted successfully")
                .build();
    }

    public ApiResponse reply(String complaintId,
                             ReplyRequest request,
                             String userId) {

        try {

            System.out.println("===== REPLY API HIT =====");

            Complaint complaint = complaintRepository.findById(complaintId)
                    .orElseThrow(() -> new RuntimeException("Invalid complaint id"));

            System.out.println("Complaint Found");

            System.out.println("Against User Id: " + complaint.getAgainstUserId());
            System.out.println("Current User Id: " + userId);

            // Authorization
            if (complaint.getAgainstUserId() == null ||
                    complaint.getAgainstUserId().equals(userId)) {

                return ApiResponse.builder()
                        .success(false)
                        .message("You are not authorized to reply to this complaint")
                        .build();
            }

            LocalDateTime now = LocalDateTime.now();

            System.out.println("Creating action steps");

            List<ActionStep> steps = request.getActions() != null
                    ? request.getActions().stream()
                    .map(a -> ActionStep.builder()
                            .description(a.getDescription())
                            .images(a.getImages())
                            .timestamp(now)
                            .build())
                    .toList()
                    : List.of();

            System.out.println("Creating reply object");

            Reply reply = Reply.builder()
                    .respondedBy(userId)
                    .status(request.getStatus())
                    .actions(steps)
                    .expectedResolutionDate(request.getExpectedResolutionDate())
                    .respondedAt(now)
                    .build();

            if (complaint.getReplies() == null) {
                complaint.setReplies(new ArrayList<>());
            }

            System.out.println("Adding reply");

            complaint.getReplies().add(reply);

            complaint.setStatus(request.getStatus());

            Complaint updated = complaintRepository.save(complaint);

            System.out.println("Complaint saved");

            try {

                UserResponse reporter =
                        userService.getUserById(complaint.getReportedBy());

                if (reporter != null && reporter.getEmail() != null) {

                    emailService.sendMessage(
                            reporter.getEmail(),
                            "Your complaint has received a new reply."
                    );

                    System.out.println("Email sent");
                }

            } catch (Exception e) {

                System.out.println("EMAIL ERROR");
                e.printStackTrace();
            }

            return ApiResponse.builder()
                    .success(true)
                    .message("Reply submitted successfully")
                    .data(updated)
                    .build();

        } catch (Exception e) {

            System.out.println("MAIN ERROR");
            e.printStackTrace();

            return ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }
    public ApiResponse updateComplaint(String complaintId, UpdateComplaintRequest request, String userId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Invalid complaint id"));

        //  Only creator can update
        if (!complaint.getReportedBy().equals(userId)) {
            return ApiResponse.builder()
                    .success(false)
                    .message("You are not authorized to update this complaint")
                    .build();
        }

        //  Optional restriction: don't allow update after reply
        if (complaint.getReplies() != null) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Cannot update complaint after it has been replied")
                    .build();
        }

        // Update fields safely
        if (request.getTitle() != null)
            complaint.setTitle(request.getTitle());

        if (request.getDescription() != null)
            complaint.setDescription(request.getDescription());

        if (request.getPriority() != null)
            complaint.setPriority(request.getPriority());

        if (request.getEvidenceImages() != null)
            complaint.setEvidenceImages(request.getEvidenceImages());

        Complaint updated = complaintRepository.save(complaint);

        return ApiResponse.builder()
                .success(true)
                .message("Complaint updated successfully")
                .data(updated)
                .build();
    }
}