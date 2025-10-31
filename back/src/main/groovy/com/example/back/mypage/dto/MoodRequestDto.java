package com.example.back.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// 감정 기록/수정 시 프론트엔드에서 받을 DTO
@Getter
@Setter // JSON -> 객체 변환용
@NoArgsConstructor
public class MoodRequestDto {
    @NotBlank(message = "공백일 수 없습니다")
    private String moodIcon; // 예: "happy"
}