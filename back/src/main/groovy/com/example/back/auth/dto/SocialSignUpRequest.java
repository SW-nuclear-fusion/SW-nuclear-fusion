// com.example.back.auth.dto.SocialSignupRequest.java

package com.example.back.auth.dto;

import com.example.back.domain.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class SocialSignUpRequest {

    // 소셜 로그인 정보 (필수)
    @NotBlank
    private String provider; // 예: "google", "kakao"

    @NotBlank
    private String providerId; // 소셜 고유 ID

    // 사용자가 추가 입력한 정보
    @NotBlank
    private String name;

    @Pattern(regexp = "^01(?:0|1|[2-9])(?:\\d{3}|\\d{4})\\d{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String phone;

    @NotNull
    private RoleType role; // 사용자가 선택한 역할 (SENIOR, GUARDIAN)

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    private String gender; // 성별 (선택적)
    private String fontSize; // 폰트 크기 (선택적)

    // Getter 및 Setter (Lombok @Data 사용 가능)
    // ...
}