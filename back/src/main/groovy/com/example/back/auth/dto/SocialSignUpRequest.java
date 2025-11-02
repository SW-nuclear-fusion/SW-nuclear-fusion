package com.example.back.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

// 소셜 유저가 7단계에서 보낼 데이터
@Getter
public class SocialSignUpRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String phone;
    @NotBlank
    private String role;
    @NotBlank
    private String fontSize;
    @NotBlank
    private String birthdate;
    @NotBlank
    private String gender;
}
