package com.example.back.oauth;

import com.example.back.auth.JwtUtil;
import com.example.back.domain.User;
import com.example.back.domain.UserRepository;
import com.example.back.domain.RoleType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

// OAuth2 로그인 성공 시, JWT 토큰을 생성하여 프론트엔드로 리다이렉트
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // 1. 인증된 사용자 정보 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 2. JWT 토큰 생성
        String userId = oAuth2User.getUserId();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("OAuth User not found"));

        String targetUrl;

        // [!] 분기 처리
        if (user.getPhone() == null) {
            // 1. 신규 유저 (추가 정보 필요)
            // 임시 토큰 (여기서는 userId 자체를 토큰처럼 사용)을 /signup/role로 보냄
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/signup/phone")
                    .queryParam("userId", userId) // [!] userId를 쿼리 파라미터로 전달
                    .toUriString();
        } else {
            // 2. 기존 유저 (로그인 완료)
            // 최종 로그인 JWT 토큰 발급
            String token = jwtUtil.generateToken(user.getUserId(), user.getRole());
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/auth/callback")
                    .queryParam("token", token)
                    .build()
                    .toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
