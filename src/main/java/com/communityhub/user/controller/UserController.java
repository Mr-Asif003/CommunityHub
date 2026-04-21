package com.communityhub.user.controller;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.user.dto.UserResponse;
import com.communityhub.user.entity.User;
import com.communityhub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private  final UserService userService;
    @GetMapping("/{id}")
    public  ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String id){
       UserResponse user=userService.getUserById(id);
        ApiResponse<UserResponse> response=ApiResponse.<UserResponse>builder()
                .success(true)
                .message("user fetched successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);

    }
    @GetMapping("/token/{id}")
    public  ResponseEntity<ApiResponse<UserResponse>> getUserByToken(@PathVariable String token){
        UserResponse user=userService.getUserByToken(token);
        ApiResponse<UserResponse> response=ApiResponse.<UserResponse>builder()
                .success(true)
                .message("user fetched successfully")
                .data(user)
                .build();
        return ResponseEntity.ok(response);

    }
    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {

        String email = authentication.getName();

        return userService.getUserByEmail(email);
    }


}
