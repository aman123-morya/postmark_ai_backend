package com.gmail.detection.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates and validates JWT access + refresh tokens.
 * Reads jwt.secret / jwt.expiration / jwt.refresh-expiration from application.properties.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_TYPE = "type";

    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        // jjwt 0.11.x needs a key of sufficient length for HS256; the configured
        // secret is padded based on its bytes if it happens to be short.
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes.length >= 32 ? keyBytes : padKey(keyBytes));
    }

    private byte[] padKey(byte[] original) {
        byte[] padded = new byte[32];
        System.arraycopy(original, 0, padded, 0, original.length);
        return padded;
    }

    public String generateToken(String email, String role, Long userId) {
        return buildToken(email, role, userId, TYPE_ACCESS, jwtExpirationMs);
    }

    public String generateRefreshToken(String email, String role, Long userId) {
        return buildToken(email, role, userId, TYPE_REFRESH, refreshExpirationMs);
    }

    private String buildToken(String email, String role, Long userId, String type, long ttlMs) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_ROLE, role);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_TYPE, type);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get(CLAIM_ROLE, String.class);
    }

    public Long extractUserId(String token) {
        return parseClaims(token).get(CLAIM_USER_ID, Long.class);
    }

    public String extractTokenType(String token) {
        return parseClaims(token).get(CLAIM_TYPE, String.class);
    }

    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public boolean isRefreshToken(String token) {
        try {
            return TYPE_REFRESH.equals(extractTokenType(token));
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isTokenValid(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
