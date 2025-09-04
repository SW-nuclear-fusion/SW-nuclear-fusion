package com.example.swnuclearfusionwas.domain.oauthjwt.oauth2;

import com.example.swnuclearfusionwas.domain.oauthjwt.dto.CustomOAuth2User;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String name = customUserDetails.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String token = jwtUtil.createJwt(name, role, 60 * 60 * 60L);

        String encodedToken = java.net.URLEncoder.encode("Bearer " + token, "UTF-8");

        Cookie cookie = new Cookie("Authorization", encodedToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 60);

        response.addCookie(cookie);

    }
}