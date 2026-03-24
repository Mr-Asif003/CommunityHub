package com.communityhub.repository;

import com.communityhub.entity.VerificationToken;

public interface VerificationTokenRepository {
    VerificationToken findByToken(String token);
}
