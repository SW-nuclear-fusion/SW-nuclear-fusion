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

    private String userId;
    private RoleType role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String userId, RoleType role) {
        super(authorities, attributes, nameAttributeKey);
        this.userId = userId;
        this.role = role;
    }
}