package com.example.back.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

// 프론트엔드 달력의 각 날짜에 표시할 데이터를 담는 DTO
@Getter
@Builder
public class DailyDataDto {
    private LocalDate date; // 날짜 (YYYY-MM-DD)
    private List<String> medicationsTaken; // 복용한 약 이름 목록
    private Integer quizCorrectCount; // 퀴즈 맞춘 개수 (null 가능)
    private String moodIcon; // 감정 아이콘 (null 가능)
}