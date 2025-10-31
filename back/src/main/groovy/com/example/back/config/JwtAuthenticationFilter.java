package com.example.back.config; // 패키지 확인

import com.example.back.auth.JwtUtil;
import com.example.back.domain.RoleType; // [!] RoleType import 추가
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger; // [!] 로깅 추가
import org.slf4j.LoggerFactory; // [!] 로깅 추가
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // 로거 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.startsWith("/api/auth") || path.startsWith("/css") || path.startsWith("/js") ||
                path.startsWith("/images") || path.equals("/favicon.ico") || path.startsWith("/h2-console") ||
                path.startsWith("/oauth2"))
        {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");
        String userId = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                if (jwtUtil.validateToken(jwt)) {
                    userId = jwtUtil.getUserIdFromToken(jwt);
                } else {
                    log.debug("Received invalid JWT token.");
                }
            } catch (Exception e) {
                log.debug("JWT Token parsing error during validation or extraction: {}", e.getMessage());
            }
        } else {
            log.trace("Authorization header is missing or does not start with Bearer for path: {}", path);
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                RoleType userRole = jwtUtil.getRoleFromToken(jwt);
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.name()));

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities); // [!] 수정된 권한 사용

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("Authentication set for user: {}", userId);

            } catch (Exception e) {
                log.error("Could not set user authentication in security context for user '{}'", userId, e);
            }
        } else if (userId == null && authorizationHeader != null) {
            log.debug("JWT token validation failed or userId could not be extracted.");
        }

        filterChain.doFilter(request, response);
    }
}