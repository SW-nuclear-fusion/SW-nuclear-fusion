package com.example.back.auth.dto;

import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class SignUpRequest {

    @NotBlank
    private String id;
    @NotBlank
    private String password;
    @NotBlank
    private String phone;
    @NotBlank
    private String role;
    @NotBlank
    private String fontSize;
    @NotBlank
    private String name;
    @NotBlank
    private String birthdate;
    @NotBlank
    private String gender;

    // DTO를 Entity로 변환하는 헬퍼 메서드
    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .userId(this.id)
                .password(passwordEncoder.encode(this.password)) // [!] 비밀번호 암호화
                .phone(this.phone)
                .role(RoleType.valueOf(this.role.toUpperCase())) // 문자열 -> Enum
                .fontSize(this.fontSize)
                .name(this.name)
                .birthdate(LocalDate.parse(this.birthdate, DateTimeFormatter.ISO_LOCAL_DATE)) // 문자열 -> 날짜
                .gender(this.gender)
                .build();
    }
}
