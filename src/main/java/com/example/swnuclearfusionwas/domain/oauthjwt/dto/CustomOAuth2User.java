package com.example.swnuclearfusionwas.domain.oauthjwt.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        if (userDTO.getRole() != null) {
            collection.add(userDTO.getRole());
        }

        return collection;
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public Long getId() {
        return userDTO.getId();
    }
}