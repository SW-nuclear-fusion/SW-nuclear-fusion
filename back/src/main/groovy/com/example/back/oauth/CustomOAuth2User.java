package com.example.back.oauth;

import com.example.back.domain.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

// Spring Security 내에서 사용될 커스텀 유저 객체
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final String userId; // DB에 저장된 최종 userId (로그인 완료 시)
    private final RoleType role;

    // 소셜 로그인 제공자 정보를 임시로 저장하여 SuccessHandler에서 활용할 수 있습니다.
    private final String provider;
    private final String providerId;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String userId,
                            RoleType role,
                            String provider,
                            String providerId) {
        super(authorities, attributes, nameAttributeKey);
        this.userId = userId;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}