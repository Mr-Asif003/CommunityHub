package com.communityhub.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinCommunityRequest {
    private String communityId;

    @NotBlank(message = "Password is required")
    private String password;
}