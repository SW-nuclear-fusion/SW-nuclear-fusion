package com.example.swnuclearfusionwas.domain.oauthjwt.jwt;

import org.springframework.stereotype.Service;

@Service
public class JWTService {

    private final JWTUtil jwtUtil;

    public JWTService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String createToken(String username, String role) {
        return jwtUtil.createJwt(username, role, 60 * 60 * 100000000L);
    }

    public Boolean isExpired(String token) {
        return jwtUtil.isExpired(token);
    }

    public String parseUsername(String tokenValue) {
        return jwtUtil.parseUsername(tokenValue);
    }
}