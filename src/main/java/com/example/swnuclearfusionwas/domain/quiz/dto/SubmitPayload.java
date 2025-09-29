package com.example.swnuclearfusionwas.domain.quiz.dto;

import java.util.List;

public record SubmitPayload(List<Answer> answers) {
    // choiceIndex == null 이면 스킵 처리
    public record Answer(Long questionId, Integer choiceIndex) { }
}
