package com.example.swnuclearfusionwas.domain.oauthjwt.service;

import com.example.swnuclearfusionwas.domain.oauthjwt.RoleType;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTUtil;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RoleService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public RoleService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public static class SetRoleRequest {
        @NotNull
        public RoleType role;
    }

    public Map<String, Object> assignRole(RoleType role, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> body = new LinkedHashMap<>();

        String token = getCookieValue(request, "Authorization");
        if (token == null || token.isBlank()) {
            body.put("error", "Authorization 쿠키가 없습니다.");
            body.put("status", 401);
            return body;
        }

        if (Boolean.TRUE.equals(jwtUtil.isExpired(token))) {
            body.put("error", "JWT가 만료되었습니다.");
            body.put("status", 401);
            return body;
        }

        String username = jwtUtil.getUsername(token);
        if (username == null || username.isBlank()) {
            body.put("error", "JWT에 username 클레임이 없습니다.");
            body.put("status", 401);
            return body;
        }

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            body.put("error", "유저를 찾을 수 없습니다: " + username);
            body.put("status", 400);
            return body;
        }

        user.setRole(role);
        userRepository.save(user);

        String authority = "ROLE_" + role.name();
        long expiryMs = 1000L * 60 * 60 * 24;
        String newToken = jwtUtil.createJwt(username, authority, expiryMs);

        Cookie cookie = new Cookie("Authorization", newToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) (expiryMs / 1000));
        response.addCookie(cookie);

        body.put("message", "role set");
        body.put("username", username);
        body.put("role", role.name());
        body.put("token", newToken);
        body.put("status", 200);
        return body;
    }

    private static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}