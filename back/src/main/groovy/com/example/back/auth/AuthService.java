package com.example.back.auth;

import com.example.back.auth.dto.LoginRequest;
import com.example.back.auth.dto.SignUpRequest;
import com.example.back.auth.dto.SocialSignUpRequest;
import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import com.example.back.domain.UserRepository;
import com.example.back.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 회원가입
    @Transactional
    public User registerUser(SignUpRequest request) {
        if (userRepository.existsByUserId(request.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 휴대폰 번호입니다.");
        }

        User user = request.toEntity(passwordEncoder);
        User saveduser = userRepository.save(user);
        userService.initializeNewUserPlants(saveduser);

        return user;
    }

    // 로컬 로그인
    public String login(LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String tok = jwtUtil.generateToken(user.getUserId(), user.getRole());
        System.out.println(tok);
        return tok;
    }

    // 아이디 중복확인
    public boolean checkUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 휴대폰 중복확인
    public boolean checkPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Transactional
    public String completeSocialSignup(SocialSignUpRequest request) {
        // 1. 소셜 ID를 기반으로 기존 사용자 또는 임시 사용자 존재 여부 확인 (필요에 따라)
        // 여기서는 미등록 사용자가 이 API를 호출했다고 가정

        // 2. userId 생성 및 중복 확인
        String userId = request.getProvider() + "_" + request.getProviderId();

        if (userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 사용자 ID입니다.");
        }

        // 3. 임시 비밀번호 생성 (DB의 password 컬럼이 NOT NULL이므로 임시값 생성)
        // 소셜 로그인 유저는 비밀번호를 사용하지 않으므로 임의의 안전한 값을 저장
        String temporaryPassword = UUID.randomUUID().toString();

        // 4. User 엔티티 생성 및 저장
        User newUser = User.builder()
                .userId(userId)
                .password(temporaryPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .birthdate(request.getBirthdate())
                .gender(request.getGender())
                .fontSize(request.getFontSize())
                .provider(request.getProvider())
                .providerId(request.getProviderId())
                // 기타 기본값 필드는 엔티티의 @ColumnDefault 값으로 자동 초기화
                .build();

        User savedUser = userRepository.save(newUser);
        userService.initializeNewUserPlants(savedUser);
        // 5. JWT 토큰 발급 및 반환
        return jwtUtil.generateToken(savedUser.getUserId(), savedUser.getRole());
    }
}
