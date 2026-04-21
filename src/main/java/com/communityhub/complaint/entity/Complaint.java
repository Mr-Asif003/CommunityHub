package com.communityhub.complaint.entity;



import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection="complaints")
public class Complaint {

    @Id
    private String id;

    private String communityId;

    private String reportedBy;

    private String againstUserId;

    private String title;

    private String description;

    private String status;

    private String priority;

    private List<String> evidenceImages;

    private LocalDate expectedResolutionDate;

    private LocalDateTime createdAt;
}