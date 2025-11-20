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

        String targetUrl;

        // ⭐ GUEST 권한을 사용하여 신규/기존 유저 분기 처리
        if (oAuth2User.getRole() == RoleType.GUEST) {
            // 1. 미등록 신규 유저 (추가 정보 필요)

            // CustomOAuth2User에 임시 저장된 providerId와 provider를 사용
            String providerId = oAuth2User.getProviderId();
            String provider = oAuth2User.getProvider();

            // ⭐ 회원가입 페이지로 리다이렉트 (소셜 데이터 전송)
            targetUrl = UriComponentsBuilder.fromUriString("http://43.201.68.38:8080/signup/phone")
                    .queryParam("provider", provider)
                    .queryParam("providerId", providerId)
                    // 필요하다면 이름도 함께 보냄
                    // .queryParam("name", oAuth2User.getName())
                    .toUriString();
        } else {
            // 2. 등록된 기존 유저 (로그인 완료)

            // CustomOAuth2UserService에서 이미 DB에 존재하는 User의 userId를 가져왔다고 가정
            String userId = oAuth2User.getUserId();

            // DB에서 유저 조회 (안전성 확보 및 RoleType 확인)
            // GUEST가 아니면 반드시 DB에 있어야 합니다.
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Registered OAuth User not found in DB"));

            // 최종 로그인 JWT 토큰 발급
            String token = jwtUtil.generateToken(user.getUserId(), user.getRole());

            targetUrl = UriComponentsBuilder.fromUriString("http://43.201.68.38:8080/auth/callback")
                    .queryParam("token", token)
                    .build()
                    .toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
