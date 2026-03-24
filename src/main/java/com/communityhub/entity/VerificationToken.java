package com.communityhub.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "tokens")
@Data
public class VerificationToken {

    @Id
    private String id;

    private String token;
    private String userId;

    private LocalDateTime expiryDate;
}
