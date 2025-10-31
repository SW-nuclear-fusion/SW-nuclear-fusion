package com.example.back.auth;

import com.example.back.domain.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationTimeMs = 1000 * 60 * 60 * 10; // 10시간
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 토큰 생성
    public String generateToken(String userId, RoleType role) {
        System.out.print("나는 행복합니다" + role.name());
        return Jwts.builder()
                .claim("userId", userId) // .claim() 메서드로 추가
                .claim("role", role.name()) // .claim() 메서드로 추가
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(secretKey) // 알고리즘 자동 추론 (HS256)
                .compact();
    }

    // 토큰에서 UserId 추출
    public String getUserIdFromToken(String token) {
        return getClaims(token).get("userId", String.class);
    }

    public RoleType getRoleFromToken(String token) {
        String role = getClaims(token).get("role", String.class);
        if (role == null) {
            log.warn("Role 클레임이 토큰에 없음");
            return RoleType.SENIOR;
        }
        try {
            return RoleType.valueOf(role); // 문자열을 RoleType Enum으로 변환
        } catch (IllegalArgumentException e) {
            log.error("Invalid role string in JWT token: {}", role, e);
            throw new IllegalArgumentException("Invalid role in token");
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getBody();
    }
}