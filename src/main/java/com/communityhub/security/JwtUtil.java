package com.communityhub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    // ================================
    // Create signing key (HS256)
    // ================================
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ================================
    // Generate JWT Token (1 hour expiry)
    // ================================ java// 15 minutes
    //1000L * 60 * 15
    //
    //// 1 hour (current)
    //1000L * 60 * 60
    //
    //// 24 hours
    //1000L * 60 * 60 * 24
    //

    /// / 7 days
    //1000L * 60 * 60 * 24 * 7
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24) // ✅ Fixed: was 1000  60 * 60
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ================================
    // Extract username (email) from token
    // ================================
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ================================
    // Extract all claims from token
    // ================================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ================================
    // Validate token — returns false if
    // expired, malformed, or bad signature
    // ================================
    public boolean validateToken(String token) {
        try {
            // ✅ parseClaimsJws() already throws ExpiredJwtException if expired
            // No need for manual date check — removed the redundant double-check
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Malformed token: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
        }

        return false;
    }
}