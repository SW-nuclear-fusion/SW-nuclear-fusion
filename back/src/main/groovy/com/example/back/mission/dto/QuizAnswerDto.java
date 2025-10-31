package com.example.back.mission.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // JSON -> 객체 변환용
public class QuizAnswerDto {
    private Long questionId;
    private String submittedAnswer;
}