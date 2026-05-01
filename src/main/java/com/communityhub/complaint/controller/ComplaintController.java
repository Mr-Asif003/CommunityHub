package com.communityhub.complaint.controller;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.complaint.dto.ComplaintRequest;
import com.communityhub.complaint.dto.ReplyRequest;
import com.communityhub.complaint.dto.UpdateComplaintRequest;
import com.communityhub.complaint.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communities/{communityId}/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // ✅ Get all complaints
    @GetMapping
    public ApiResponse getComplaint(@PathVariable String communityId) {
        return complaintService.getComplaints(communityId);
    }

    // ✅ Create complaint
    @PostMapping
    public ApiResponse createComplaint(
            @RequestBody ComplaintRequest complaint,
            @PathVariable String communityId,
            @RequestHeader("userId") String userId
    ) {


        return complaintService.createComplaint(complaint, communityId, userId);
    }

    // ✅ Reply to complaint
    @PostMapping("/{complaintId}/reply")
    public ApiResponse replyToComplaint(
            @PathVariable String complaintId,
            @RequestBody ReplyRequest request,
            @RequestHeader("userId") String userId
    ) {
        return complaintService.reply(complaintId, request, userId);
    }

    // ✅ Update complaint (by reporter)
    @PutMapping("/{complaintId}")
    public ApiResponse updateComplaint(
            @PathVariable String complaintId,
            @RequestBody UpdateComplaintRequest request,
            @RequestHeader("userId") String userId
    ) {
        return complaintService.updateComplaint(complaintId, request, userId);
    }
}