package com.example.back.mission.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuizResultDto {
    private int correctCount; // 맞춘 개수 (프론트엔드로 전달)
    private int totalCount;   // 전체 개수 (5)
    private String status;    // 진단 상태: "정상", "의심", "위험"
    private String message;   // 상태에 따른 메시지
    private List<RewardDto> rewards; // 최종 지급된 보상 목록 (물, 하트 등)
}