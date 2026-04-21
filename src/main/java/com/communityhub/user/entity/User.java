package com.communityhub.user.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

}
