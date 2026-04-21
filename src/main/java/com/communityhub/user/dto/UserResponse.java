package com.communityhub.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
