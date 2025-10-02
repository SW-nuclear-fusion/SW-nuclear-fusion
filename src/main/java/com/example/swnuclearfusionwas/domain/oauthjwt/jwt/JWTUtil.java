package com.example.swnuclearfusionwas.domain.oauthjwt.jwt;

import com.example.swnuclearfusionwas.domain.oauthjwt.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(Long id, String role, Long expiredMs) {
            return Jwts.builder()
                    .claim("id", id)     // 여기에는 DB PK id
                    .claim("role", role)     // enum -> String
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiredMs))
                    .signWith(secretKey)
                    .compact();
    }

    public String getId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)  // ✅ 여기 secretKey
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", String.class);
    }

    public Long parseUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("id", Number.class).longValue();
    }

    public String parseRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("role", String.class);
    }

}
