package com.example.back.oauth;

import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import com.example.back.domain.UserRepository; // 경로 확인 필요
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. 소셜 ID 및 이름 추출 (헬퍼 메서드 사용)
        String providerId = getProviderId(provider, attributes);
        String name = getName(provider, attributes);
        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 2. DB에서 유저 조회
        User user = userRepository.findByProviderAndProviderId(provider, providerId).orElse(null);

        if (user != null) {
            // ⭐ 1. 등록된 기존 유저: 업데이트 및 로그인 처리
            //user.updateSocialInfo(name); // name만 업데이트하는 메소드 사용
            //userRepository.save(user);

            return new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                    attributes,
                    nameAttributeKey, // 소셜 서비스별 고유 키
                    user.getUserId(),
                    user.getRole(),
                    provider,
                    providerId
            );
        } else {
            // ⭐ 2. 미등록 신규 유저: GUEST 권한 부여 후 리다이렉션 유도
            return new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")),
                    attributes,
                    nameAttributeKey,
                    null, // 최종 userId 없음
                    RoleType.GUEST,
                    provider,
                    providerId
            );
        }
    }

    /** 소셜 서비스별 고유 ID 추출 */
    private String getProviderId(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("sub");
        }
        if ("kakao".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        }
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response == null) { throw new OAuth2AuthenticationException("Naver response is null"); }
            return (String) response.get("id");
        }
        throw new OAuth2AuthenticationException("Unknown Provider: " + provider);
    }

    /** 소셜 서비스별 이름 추출 */
    private String getName(String provider, Map<String, Object> attributes) {
        if ("google".equals(provider)) {
            return (String) attributes.get("name");
        }
        if ("kakao".equals(provider)) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            return (String) properties.get("nickname");
        }
        if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response == null) { throw new OAuth2AuthenticationException("Naver response is null"); }
            return (String) response.get("name");
        }
        return null;
    }
}