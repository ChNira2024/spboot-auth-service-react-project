package com.spboot.auth.security.service;

import com.spboot.auth.entity.Role;
import com.spboot.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
@Getter
public class JwtService2 {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService2(
            @Value("${spring.security.jwt.secret}") String secret,
            @Value("${spring.security.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${spring.security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds,
            @Value("${spring.security.jwt.issuer}") String issuer) {

        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("Invalid JWT secret");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
    }

    // =========================
    // 🔐 TOKEN GENERATION
    // =========================

    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        List<String> roles = Optional.ofNullable(user.getRoles())
                .orElse(Set.of())
                .stream()
                .map(Role::getName)
                .toList();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getId().toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("typ", "access")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user, String jti) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setId(jti)
                .setSubject(user.getId().toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // =========================
    // 🔍 TOKEN PARSING (ONLY ONCE)
    // =========================

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =========================
    // 🔍 CLAIMS UTIL METHODS
    // =========================

    public boolean isAccessToken(Claims claims) {
        return "access".equals(claims.get("typ"));
    }

    public boolean isRefreshToken(Claims claims) {
        return "refresh".equals(claims.get("typ"));
    }

    public UUID getUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String getJti(Claims claims) {
        return claims.getId();
    }

    public String getEmail(Claims claims) {
        return (String) claims.get("email");
    }

    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }
}