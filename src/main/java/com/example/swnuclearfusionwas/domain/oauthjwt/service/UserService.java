package com.example.swnuclearfusionwas.domain.oauthjwt.service;

import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignInReqDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignInResDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignUpReqDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTService;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public Long signup(SignUpReqDto req) {
        if (userRepository.existsByUserId(req.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByPhone(req.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        if (!req.getPhone().matches("\\d{11}")) {
            throw new IllegalArgumentException("전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.");
        }

        String encodedPw = passwordEncoder.encode(req.getUserPW());

        UserEntity user = new UserEntity();
        user.setUserId(req.getUserId());
        user.setUserPW(encodedPw);
        user.setName(req.getName());
        user.setRole(req.getRole());
        user.setPhone(req.getPhone());

        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public SignInResDto login(SignInReqDto req) {
        UserEntity user = userRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getUserPW(), user.getUserPW())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtService.createToken(user.getUserId(), String.valueOf(user.getRole()));

        return new SignInResDto(token, user.getName(), user.getRole());
    }
}