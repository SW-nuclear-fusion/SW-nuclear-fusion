package com.example.back.config;

import com.example.back.oauth.CustomOAuth2UserService;
import com.example.back.oauth.OAuth2SuccessHandler;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
=======
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
>>>>>>> 1e287e9 (demo v1)
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
<<<<<<< HEAD
=======
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
>>>>>>> 1e287e9 (demo v1)
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
<<<<<<< HEAD
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",  // 로컬 개발용
                "http://43.201.68.38:8080"      // EC2 서버 IP (8080 포트를 안 쓴다면 포트번호 제외)
        ));
=======
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // 프론트엔드 주소
>>>>>>> 1e287e9 (demo v1)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
<<<<<<< HEAD
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 경로별 접근 권한 설정
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        // React 정적 파일 및 루트 경로 허용
                        .requestMatchers("/", "/index.html", "/assets/**", "/static/**", "/*.ico", "/*.json", "/*.png").permitAll()
                        .requestMatchers("/{path:[^\\.]*}", "/**/{path:[^\\.]*}").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()
                        .requestMatchers("/api/user/**").authenticated()
                        .anyRequest().authenticated()
                );

        // OAuth2 로그인 설정
=======
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathRequest.toH2Console())
                        .disable()
                )
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 사용 안 함

        // 경로별 접근 권한 설정 수정
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()        // 인증 API 모두 허용
                        .requestMatchers("/login/oauth2/**").permitAll()  // OAuth2 콜백 허용
                        .requestMatchers(PathRequest.toH2Console()).permitAll() // [!] H2 콘솔 경로 허용
                        .requestMatchers("/api/user/**").authenticated()
                        .anyRequest().authenticated()                     // 그 외 모든 요청은 인증 필요
                );

        // H2 콘솔 iframe 로드를 위한 설정 추가
        http
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // 같은 출처의 프레임 허용
                );

        // OAuth2 로그인 설정 (기존과 동일)
>>>>>>> 1e287e9 (demo v1)
        http
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

<<<<<<< HEAD
        // JWT 필터 추가
=======
>>>>>>> 1e287e9 (demo v1)
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}