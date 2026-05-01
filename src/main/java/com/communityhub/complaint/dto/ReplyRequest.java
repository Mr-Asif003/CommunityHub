package com.communityhub.complaint.dto;

import com.communityhub.complaint.entity.embedded.ActionStep;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReplyRequest {

    private String status;

    private LocalDate expectedResolutionDate;

    private List<ActionStepDTO> actions;
}
