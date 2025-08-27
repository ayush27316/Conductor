package com.conductor.core.security;

import com.conductor.core.dto.permission.PermissionDTO;
import com.conductor.core.model.user.User;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.util.OptionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SecretKey getSecretKey() {
        // Ensure the secret key is at least 32 bytes for HMAC-SHA256
        if (jwtSecretKey.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        try {

            String permissionsJson = objectMapper.writeValueAsString(user.getPermissions());

            return Jwts.builder()
                    .subject(user.getUsername())
                    .claim("user_external_id", user.getExternalId())
                    .claim("permissions", permissionsJson)
                    .claim("user_role", user.getRole().getLabel())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                    .signWith(getSecretKey())
                    .compact();
        } catch (JsonProcessingException e) {
            log.error("Error serializing permissions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize permissions", e);
        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }


    public String getExternalId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("user_external_id", String.class);
        } catch (Exception e) {
            log.error("Error extracting externalId from JWT: {}", e.getMessage(), e);
            throw new RuntimeException("Error extracting externalId", e);
        }
    }

    public List<PermissionDTO> getPermissions(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String permissionsJson = claims.get("permissions", String.class);
            if (permissionsJson == null) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(
                    permissionsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PermissionDTO.class)
            );
        } catch (Exception e) {
            log.error("Error extracting permissions from JWT: {}", e.getMessage(), e);
            throw new RuntimeException("Error extracting permissions", e);
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


    public UserRole getUserRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return UserRole.fromValue(claims.get("user_role", String.class));
        } catch (Exception e) {
            log.error("Error extracting externalId from JWT: {}", e.getMessage(), e);
            throw new RuntimeException("Error extracting externalId", e);
        }
    }
}
