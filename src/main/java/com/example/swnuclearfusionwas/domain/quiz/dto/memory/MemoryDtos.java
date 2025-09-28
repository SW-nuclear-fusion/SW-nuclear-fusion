package com.example.swnuclearfusionwas.domain.quiz.dto.memory;

import java.util.List;
import java.util.UUID;

public class MemoryDtos {
    public record StartRequest(String difficulty, int count) {}
    public record Trial(String stimulus) {}
    public record StartResponse(UUID sessionId, List<Trial> trials, int showMillis, int answerMillis) {}
    public record SubmitRequest(UUID sessionId, List<String> answers) {}
    public record SubmitResponse(int total, int correct, int rewardPoints) {}
}
