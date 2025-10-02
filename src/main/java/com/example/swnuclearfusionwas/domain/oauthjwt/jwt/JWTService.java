package com.example.swnuclearfusionwas.domain.oauthjwt.jwt;

import com.example.swnuclearfusionwas.domain.oauthjwt.RoleType;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

    private final JWTUtil jwtUtil;

    public JWTService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String createToken(Long id, String roleName) {
        return jwtUtil.createJwt(id, roleName, 60 * 60 * 1000L);
    }

    public Long parseUserId(String token) {
        return jwtUtil.parseUserId(token);
    }

    public Boolean isExpired(String token) {
        return jwtUtil.isExpired(token);
    }
}