package com.example.back.auth;

import com.example.back.auth.dto.LoginRequest;
import com.example.back.auth.dto.SignUpRequest;
import com.example.back.auth.dto.SocialSignUpRequest;
import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import com.example.back.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

        userRepository.save(user);
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
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소셜 유저입니다."));

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 휴대폰 번호입니다.");
        }

        user.setPhone(request.getPhone());
        user.setRole(RoleType.valueOf(request.getRole().toUpperCase()));
        user.setFontSize(request.getFontSize());
        user.setBirthdate(LocalDate.parse(request.getBirthdate(), DateTimeFormatter.ISO_LOCAL_DATE));
        user.setGender(request.getGender());

        return jwtUtil.generateToken(user.getUserId(), user.getRole());
    }
}
