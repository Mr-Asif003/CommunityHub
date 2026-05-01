package com.communityhub.complaint.entity.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionStep {

    private String description;          // e.g. "Issued warning to user"
    private List<String> images;         // proof images (URLs)
    private LocalDateTime timestamp;
}
