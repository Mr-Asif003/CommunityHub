package com.communityhub.poll.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreatePollRequest {
    private String communityId;
    private String createdBy;
    private String question;
    private List<String> options;
    private LocalDateTime expiresAt;
    private boolean anonymous;
}
