package com.communityhub.complaint.dto;

import com.communityhub.complaint.entity.Complaint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintRequest {

    private String title;
    private String againstUserId;

    private String description;

    private String priority;

    private String reporterLastDate;

    private List<String> evidenceImages;
}