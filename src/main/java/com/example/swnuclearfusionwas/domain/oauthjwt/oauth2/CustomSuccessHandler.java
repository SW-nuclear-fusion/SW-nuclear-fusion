package com.example.swnuclearfusionwas.domain.oauthjwt.oauth2;

import com.example.swnuclearfusionwas.domain.oauthjwt.dto.CustomOAuth2User;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;

    public CustomSuccessHandler(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = principal.getId();
        String roleName = principal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority) // "ROLE_SENIOR" 같은 값
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .orElse(null);

        String token = jwtService.createToken(userId, roleName);
        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

    }
}
