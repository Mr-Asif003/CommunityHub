package com.communityhub.user.controller;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.user.dto.UserResponse;
import com.communityhub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String id) {

        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User fetched successfully")
                        .data(user)
                        .build()
        );
    }


    @GetMapping("/token/{token}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByToken(@PathVariable String token) {

        UserResponse user = userService.getUserByToken(token);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User fetched successfully")
                        .data(user)
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body(
                    ApiResponse.builder()
                            .success(false)
                            .message("Unauthorized")
                            .build()
            );
        }

        String email = authentication.getName();

        UserResponse user = userService.getUserByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Current user fetched successfully")
                        .data(user)
                        .build()
        );
    }
}