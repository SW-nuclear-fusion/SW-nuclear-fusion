package com.example.back.user.dto;

import com.example.back.domain.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class SocialSignupRequestDto {

    // 1. OAuth2SuccessHandler에서 쿼리 파라미터로 받은 userId
    @NotBlank
    private String userId;

    // 2. 전화번호 (필수)
    @NotBlank
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", message = "유효하지 않은 전화번호 형식입니다.")
    private String phone;

    // 3. 역할 (필수)
    @NotNull
    private RoleType role; // SENIOR 또는 GUARDIAN

    // 4. 생년월일 (필수)
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    // 5. 성별 (필수, 필요하다면)
    private String gender;

    // 6. 폰트 크기 (선택, 기본값은 DB에서 처리 가능)
    private String fontSize;
}