package com.communityhub.complaint.entity.embedded;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reply {


    private String respondedBy;
    private String status;

    @Builder.Default
    private List<ActionStep> actions = new ArrayList<>();

    private LocalDate expectedResolutionDate;
    private LocalDateTime respondedAt;
}
