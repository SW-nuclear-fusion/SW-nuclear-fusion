package com.example.swnuclearfusionwas.domain.quiz.dto;

import java.util.List;

public record QuestionDto(Long id, String text, List<String> choices) { }
