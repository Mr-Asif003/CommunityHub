package com.communityhub.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tokens")
public class VerificationToken {

    @Id
    private String id;

    private String token;
    private String userId;

    @Indexed(name = "expiry_ttl", expireAfter = "0s")
    private Date expiryDate;
}