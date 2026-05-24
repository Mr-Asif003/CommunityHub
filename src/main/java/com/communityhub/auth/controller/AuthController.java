package com.communityhub.auth.controller;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.auth.dto.AuthResponse;
import com.communityhub.auth.dto.LoginRequest;
import com.communityhub.auth.dto.RegisterRequest;
import com.communityhub.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:8081") // React/Next frontend
public class AuthController {

    @Autowired
    AuthService authService;
    @GetMapping
    public String home(){
        return "home";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        return ResponseEntity.ok(authService.verify(token));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        ApiResponse<?> response = authService.login(req);

        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }

        AuthResponse auth = (AuthResponse) response.getData();

        ResponseCookie cookie = ResponseCookie.from("jwt", auth.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(86400)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(ApiResponse.builder()
                        .success(true)
                        .message("Login successful")
                        .build());
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body("Logged out");
    }

}
