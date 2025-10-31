package com.example.back.oauth;

import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import com.example.back.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

// Google, Kakao 로그인 성공 시 DB에 유저를 저장하거나 업데이트
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. 소셜 서비스 이름 (google, kakao 등)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 2. 소셜 서비스의 유저 정보 (JSON)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 3. 소셜 서비스의 유저 고유 ID
        String providerId = getProviderId(provider, attributes);
        String name = getName(provider, attributes);
        String userId = provider + "_" + providerId; // (예: "google_123456789")

        // 4. DB에서 유저 조회 또는 신규 생성 (로그인 또는 자동 회원가입)
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .map(existingUser -> existingUser.updateSocialInfo(name)) // 이미 있으면 이름만 업데이트
                .orElseGet(() -> createNewSocialUser(userId, name, provider, providerId)); // 없으면 새로 생성

        userRepository.save(user);

        // 5. Spring Security가 인식할 수 있는 유저 객체로 변환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "name", // nameAttributeKey (중요하지 않음)
                user.getUserId(),
                user.getRole()
        );
    }

    // 소셜 서비스별 고유 ID 추출
    private String getProviderId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        }
        if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        }
        throw new OAuth2AuthenticationException("Unknown Provider");
    }

    // 소셜 서비스별 이름 추출
    private String getName(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        }
        if ("kakao".equals(provider)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return (String) properties.get("nickname");
        }
        throw new OAuth2AuthenticationException("Unknown Provider");
    }

    // 새 소셜 유저 생성 (자동 회원가입)
    private User createNewSocialUser(String userId, String name, String provider, String providerId) {
        // 소셜 로그인 유저는 필수 정보가 부족하므로, 임시값으로 채웁니다.
        // TODO: (추후) 소셜 로그인 유저에게 7단계 회원가입을 유도하는 로직 필요
        return User.builder()
                .userId(userId)
                .password(UUID.randomUUID().toString()) // 비밀번호 임시값
                .name(name)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}