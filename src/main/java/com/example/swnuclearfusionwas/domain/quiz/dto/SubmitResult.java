package com.example.swnuclearfusionwas.domain.quiz.dto;

import java.util.List;

public record SubmitResult(
        int total,
        int correct,
        List<Long> wrongQuestionIds,
        int rewardPoints
) { }
