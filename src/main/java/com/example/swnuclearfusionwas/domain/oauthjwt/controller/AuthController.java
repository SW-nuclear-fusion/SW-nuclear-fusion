package com.example.swnuclearfusionwas.domain.oauthjwt.controller;

import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignInReqDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignInResDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignUpReqDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpReqDto signUpReqDto) {
        try {
            userService.signup(signUpReqDto);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<SignInResDto> login(@RequestBody SignInReqDto signInReqDto) {
        try {
            SignInResDto signInResDto = userService.login(signInReqDto);
            return ResponseEntity.ok(signInResDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SignInResDto(null, e.getMessage(), null));
        }
    }
}