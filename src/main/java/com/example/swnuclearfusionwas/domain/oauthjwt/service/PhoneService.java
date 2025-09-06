package com.example.swnuclearfusionwas.domain.oauthjwt.service;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTUtil;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PhoneService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public PhoneService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Getter @Setter
    public static class SetPhoneRequest {
        @NotBlank
        private String phone;  // 숫자만, 길이 10~11
    }

    public Map<String, Object> setPhone(SetPhoneRequest req,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
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

        String phone = req.getPhone();
        if (phone == null || !phone.matches("\\d{11}")) {
            body.put("error", "전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.");
            body.put("status", 400);
            return body;
        }

        boolean phoneExists = userRepository.existsByPhone(phone);
        if (phoneExists && (user.getPhone() == null || !phone.equals(user.getPhone()))) {
            body.put("error", "이미 등록된 전화번호입니다.");
            body.put("status", 409);
            return body;
        }

        user.setPhone(phone);
        userRepository.save(user);

        body.put("message", "phone set");
        body.put("username", username);
        body.put("phone", phone);
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