package com.communityhub.user.service;

import com.communityhub.auth.entity.VerificationToken;
import com.communityhub.auth.repository.VerificationTokenRepository;
import com.communityhub.user.dto.UserResponse;
import com.communityhub.user.entity.User;
import com.communityhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository vt;

    public UserResponse getUserById(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse getUserByToken(String token){
        VerificationToken v = vt.findByToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Token not found"));
        return getUserById(v.getUserId());
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
         System.out.println("user details"+user);
        return mapToResponse(user);
    }


    private UserResponse mapToResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}