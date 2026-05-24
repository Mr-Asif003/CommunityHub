package com.communityhub.user.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String role;

    private boolean enabled;
    private boolean verified;

    private String profileImage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // FIX: renamed from 'LastSeen' to 'lastSeen' (Java convention: camelCase)
    private Instant lastSeen;

    // FIX: renamed from 'isOnline' to 'online' to avoid Lombok/Jackson
    //      conflict (Lombok generates isOnline() for a field named isOnline,
    //      producing a duplicate getter and confusing JSON serialisation)
    private boolean online;
}