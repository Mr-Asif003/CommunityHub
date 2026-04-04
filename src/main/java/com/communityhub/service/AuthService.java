package com.communityhub.service;

import com.communityhub.dto.ApiResponse;
import com.communityhub.dto.AuthResponse;
import com.communityhub.dto.LoginRequest;
import com.communityhub.dto.RegisterRequest;
import com.communityhub.entity.User;
import com.communityhub.entity.VerificationToken;
import com.communityhub.exception.UserNotFoundException;
import com.communityhub.repository.UserRepository;
import com.communityhub.repository.VerificationTokenRepository;
import com.communityhub.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired UserRepository userRepo;
    @Autowired VerificationTokenRepository tokenRepo;
    @Autowired PasswordEncoder encoder;
    @Autowired EmailService emailService;
    @Autowired JwtUtil jwtUtil;

    public ApiResponse<?> register(RegisterRequest req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .build();
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));

        userRepo.save(user);

        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUserId(user.getId());
        vt.setExpiryDate(
                Date.from(
                        LocalDateTime.now()
                                .plusHours(24)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                )
        );

        tokenRepo.save(vt);

        emailService.sendVerificationEmail(user.getEmail(), token);

        return ApiResponse.builder()
                .success(true)
                .message("Verification email sent")
                .build();
    }

    public ApiResponse<?> verify(String token) {

        VerificationToken vt = tokenRepo.findByToken(token);

        if (vt == null) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Invalid token")
                    .build();
        }

        if (vt.getExpiryDate().before(new Date())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Token expired")
                    .build();
        }

        User user = userRepo.findById(vt.getUserId()).orElseThrow();
        user.setEnabled(true);
        userRepo.save(user);

        return ApiResponse.builder()
                .success(true)
                .message("Account verified successfully")
                .build();
    }

    public ApiResponse<?> login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Verify email first")
                    .build();
        }

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Invalid credentials")
                    .build();
        }

        String token = jwtUtil.generateToken(user.getEmail());

        AuthResponse auth = AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return ApiResponse.builder()
                .success(true)
                .message("Login successful")
                .data(auth)
                .build();
    }
}