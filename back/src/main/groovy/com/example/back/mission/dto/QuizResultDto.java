package com.example.back.mission.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizResultDto {
    private int correctCount;
    private int totalCount;
    private RewardDto reward; // 보상 정보 (null일 수 있음)
}