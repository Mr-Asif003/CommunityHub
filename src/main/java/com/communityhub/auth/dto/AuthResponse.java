package com.communityhub.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String fullName;
    private int phone;
    private String token;
    private String email;
    private String role;
}