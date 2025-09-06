package com.example.swnuclearfusionwas.domain.oauthjwt.controller;

import com.example.swnuclearfusionwas.domain.oauthjwt.RoleType;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTUtil;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Auth", description = "인증/역할 관련 API")
@RestController
@RequestMapping("/api/auth")
public class RoleController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public RoleController(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public static class SetRoleRequest {
        @NotNull
        public RoleType role;
    }

    @Operation(
            summary = "역할 선택(시니어/보호자)",
            description = """
            소셜 로그인 직후, 쿠키 Authorization(JWT)에서 username을 읽어 해당 유저의 역할을 설정합니다.
            성공 시 ROLE_* 권한이 담긴 새 JWT를 생성하여 Authorization 쿠키로 재설정합니다.
            * Bearer 접두사 없이 순수 토큰을 쿠키에 저장/사용합니다.
            """
    )
    @PostMapping("/role")
    public ResponseEntity<?> setRole(
            @RequestBody SetRoleRequest requestBody,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String token = getCookieValue(request, "Authorization");
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Authorization 쿠키가 없습니다.");
        }

        if (Boolean.TRUE.equals(jwtUtil.isExpired(token))) {
            return ResponseEntity.status(401).body("JWT가 만료되었습니다.");
        }

        String username = jwtUtil.getUsername(token);
        if (username == null || username.isBlank()) {
            return ResponseEntity.status(401).body("JWT에 username 클레임이 없습니다.");
        }

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("유저를 찾을 수 없습니다: " + username);
        }

        RoleType role = requestBody.role;
        user.setRole(role);
        userRepository.save(user);

        String authority = "ROLE_" + role.name();
        long expiryMs = 1000000000L * 60 * 60 * 24;
        String newToken = jwtUtil.createJwt(username, authority, expiryMs);

        Cookie cookie = new Cookie("Authorization", newToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int)(expiryMs / 1000));
        response.addCookie(cookie);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "role set");
        body.put("username", username);
        body.put("role", role.name());
        body.put("token", newToken);
        return ResponseEntity.ok(body);
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