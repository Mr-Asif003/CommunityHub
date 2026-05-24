package com.communityhub.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// FIX: added @NoArgsConstructor and @AllArgsConstructor so Jackson can
//      deserialise the DTO and so mapToDto() in ChatRoomService compiles
//      without requiring the @Builder workaround.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // FIX: added online / lastSeen so callers can expose presence status
    //      without a second network call.  Both fields are optional and default
    //      to false / null, so existing code that does not set them still works.
    private boolean online;
    private java.time.Instant lastSeen;
}