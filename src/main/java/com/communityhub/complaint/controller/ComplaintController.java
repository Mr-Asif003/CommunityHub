package com.communityhub.complaint.controller;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.complaint.dto.ComplaintRequest;
import com.communityhub.complaint.dto.ReplyRequest;
import com.communityhub.complaint.dto.UpdateComplaintRequest;
import com.communityhub.complaint.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communities/{communityId}/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // ✅ Get all complaints
    @GetMapping
    public ApiResponse getComplaints(
            @PathVariable String communityId,
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("User not authenticated")
                    .build();
        }

        return complaintService.getComplaints(communityId);
    }

    // ✅ Create complaint
    @PostMapping
    public ApiResponse createComplaint(
            @Valid @RequestBody ComplaintRequest complaint,
            @PathVariable String communityId,
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("User not authenticated")
                    .build();
        }

        return complaintService.createComplaint(
                complaint,
                communityId,
                auth.getName()
        );
    }

    // ✅ Reply to complaint
    @PostMapping("/{complaintId}/reply")
    public ApiResponse replyToComplaint(
            @PathVariable String communityId,
            @PathVariable String complaintId,
            @Valid @RequestBody ReplyRequest request,
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("User not authenticated")
                    .build();
        }

        return complaintService.reply(
                complaintId,
                request,
                auth.getName()
        );
    }

    // ✅ Update complaint
    @PutMapping("/{complaintId}")
    public ApiResponse updateComplaint(
            @PathVariable String communityId,
            @PathVariable String complaintId,
            @Valid @RequestBody UpdateComplaintRequest request,
            Authentication auth
    ) {

        if (auth == null || !auth.isAuthenticated()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("User not authenticated")
                    .build();
        }

        return complaintService.updateComplaint(
                complaintId,
                request,
                auth.getName()
        );
    }
}