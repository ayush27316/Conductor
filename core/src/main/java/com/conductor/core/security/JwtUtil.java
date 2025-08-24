package com.conductor.core.security;

import com.conductor.core.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        // Ensure the secret key is at least 32 bytes for HMAC-SHA256
        if (jwtSecretKey.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        try {
            return Jwts.builder()
                    .subject(user.getUsername())
                    .claim("userId", user.getId().toString())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                    .signWith(getSecretKey())
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("JWT token expired", e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new RuntimeException("Malformed JWT token", e);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or null: {}", e.getMessage());
            throw new RuntimeException("JWT token is empty or null", e);
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getPermissions(String token) {
        // TODO: Implement permission extraction from token
        return null;
    }
}
