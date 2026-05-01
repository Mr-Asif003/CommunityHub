package com.communityhub.complaint.dto;

import lombok.Data;
import java.util.List;

@Data
public class ActionStepDTO {

    private String description;

    private List<String> images;
}