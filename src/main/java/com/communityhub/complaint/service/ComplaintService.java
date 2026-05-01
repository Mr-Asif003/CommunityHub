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
                .existsByUserIdAndCommunityId(request.getAgainstUserId(), communityId);


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
        if(com.getReportedBy()!=userId){
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

    public ApiResponse reply(String complaintId, ReplyRequest request, String userId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Invalid complaint id"));


        if (complaint.getAgainstUserId() == null || !complaint.getAgainstUserId().equals(userId)) {
            return ApiResponse.builder()
                    .success(false)
                    .message("You are not authorized to reply to this complaint")
                    .build();
        }


        LocalDateTime now = LocalDateTime.now();


        List<ActionStep> steps = request.getActions() != null
                ? request.getActions().stream()
                .map(a -> ActionStep.builder()
                        .description(a.getDescription())
                        .images(a.getImages())
                        .timestamp(now)
                        .build())
                .toList()
                : List.of();
        if (steps != null) {
            steps.forEach(step -> step.setTimestamp(LocalDateTime.now()));
        }


        Reply reply = Reply.builder()
                .respondedBy(userId)
                .status(request.getStatus())
                .actions(steps)
                .expectedResolutionDate(request.getExpectedResolutionDate())
                .respondedAt(LocalDateTime.now())
                .build();

        complaint.setReply(reply);
        complaint.setStatus(request.getStatus());

        Complaint updated = complaintRepository.save(complaint);
        UserResponse u=userService.getUserById(complaint.getReportedBy());
        emailService.sendMessage(updated.getReportedBy(),"New update: You have got reply of your complaint");
        return ApiResponse.builder()
                .success(true)
                .message("Reply with actions submitted successfully")
                .data(updated)
                .build();
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
        if (complaint.getReply() != null) {
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