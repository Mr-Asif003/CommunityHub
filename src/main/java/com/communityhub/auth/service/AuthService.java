package com.communityhub.auth.service;

import com.communityhub.auth.dto.*;
import com.communityhub.auth.entity.VerificationToken;
import com.communityhub.auth.repository.VerificationTokenRepository;
import com.communityhub.exception.UserNotFoundException;
import com.communityhub.notification.service.EmailService;
import com.communityhub.security.JwtUtil;
import com.communityhub.user.entity.User;
import com.communityhub.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    // ========================= REGISTER =========================
    public ApiResponse<?> register(RegisterRequest req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .build();
        }

        if (userRepo.findByPhone(req.getPhone()).isPresent()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Mobile number already exists")
                    .build();
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(false);

        userRepo.save(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();

        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUserId(user.getId());

        // ⏱ expiry set to 24 hours (change to plusMinutes(1) for testing)
        vt.setExpiryDate(
                Date.from(
                        LocalDateTime.now()
                                .plusMinutes(1)
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

    // ========================= VERIFY =========================
    public ApiResponse<?> verify(String token) {

        VerificationToken vt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        // Check expiry
        if (vt.getExpiryDate().before(new Date())) {
            tokenRepo.delete(vt);

            return ApiResponse.builder()
                    .success(false)
                    .message("Token expired")
                    .build();
        }

        User user = userRepo.findById(vt.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEnabled()) {
            return ApiResponse.builder()
                    .success(true)
                    .message("Account already verified")
                    .build();
        }

        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);

        // Clean up token after use
        tokenRepo.delete(vt);

        return ApiResponse.builder()
                .success(true)
                .message("Account verified successfully")
                .build();
    }

    // ========================= LOGIN =========================
    public ApiResponse<?> login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

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

        String jwt = jwtUtil.generateToken(user.getEmail());

        AuthResponse auth = AuthResponse.builder()
                .token(jwt)
                .fullName(user.getFullName())
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