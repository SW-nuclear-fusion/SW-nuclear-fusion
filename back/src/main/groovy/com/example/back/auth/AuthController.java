package com.example.back.auth;

import com.example.back.auth.dto.LoginRequest;
import com.example.back.auth.dto.TokenResponse;
import com.example.back.auth.dto.SignUpRequest;
import com.example.back.auth.dto.SocialSignUpRequest;
import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth") // 공통 경로
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 (POST /api/auth/signup)
     * 프론트 7단계 완료 시 이 API 호출
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {try {
        // 1. 회원가입 실행하고 User 객체 받기
        User registeredUser = authService.registerUser(request);

        // 2. 받은 User 정보로 JWT 토큰 생성
        String token = jwtUtil.generateToken(registeredUser.getUserId(), registeredUser.getRole());

        // 3. 토큰을 TokenResponse에 담아 201 Created 상태로 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenResponse(token));

    } catch (IllegalArgumentException e) {
        // 중복 등 예외 발생 시 409 Conflict 상태와 에러 메시지 반환
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body((TokenResponse) Map.of("message", e.getMessage()));
    }
    }

    @PostMapping("/signup-social")
    public ResponseEntity<?> socialSignup(@Valid @RequestBody SocialSignUpRequest request) {
        try {
            String token = authService.completeSocialSignup(request);
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 아이디 중복 확인 (GET /api/auth/check-id?id=...)
     */
    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestParam("id") String userId) {
        boolean isAvailable = !authService.checkUserIdExists(userId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    /**
     * 휴대폰 중복 확인 (GET /api/auth/check-phone?phone=...)
     */
    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhone(@RequestParam("phone") String phone) {
        boolean isAvailable = !authService.checkPhoneExists(phone);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }
}