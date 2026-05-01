package com.communityhub.complaint.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateComplaintRequest {
    private String title;
    private String description;
    private String priority;
    private List<String> evidenceImages;
}
