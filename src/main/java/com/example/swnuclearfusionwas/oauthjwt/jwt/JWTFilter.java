package com.example.swnuclearfusionwas.oauthjwt.jwt;

import com.example.swnuclearfusionwas.oauthjwt.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    public JWTFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청에서 쿠키 가져오기
        Cookie[] cookies = request.getCookies();
        String authToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    authToken = cookie.getValue();
                    break;
                }
            }
        }

        // 토큰이 없으면 그냥 다음 필터로 넘김
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 제거하고 실제 토큰 값만 추출
        String tokenValue = authToken.replace("Bearer ", "");

        // 토큰이 유효한지 확인
        if (jwtService.isExpired(tokenValue)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has expired.");
            return;
        }

        // 토큰에서 사용자 정보 추출
        Long userId = jwtService.parseUserId(tokenValue);

        // 사용자 정보가 유효한지 확인
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not authenticated.");
            return;
        }

        // 인증된 사용자 정보를 SecurityContext에 설정
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰이 유효한 경우, JWT를 쿠키에 담아서 클라이언트에 전달
        Cookie cookie = new Cookie("Authorization", "Bearer " + tokenValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);  // XSS 보호
        cookie.setMaxAge(60 * 60 * 60); // 만료 시간 설정 (예: 1시간)
        response.addCookie(cookie);

        // 필터 체인 진행
        filterChain.doFilter(request, response);
    }
}