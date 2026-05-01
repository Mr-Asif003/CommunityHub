package com.communityhub.poll.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PollResponse {
    private String id;
    private String question;
    private List<String> options;
    private LocalDateTime expiresAt;
    private String decisionMade;
}