package com.example.swnuclearfusionwas.domain.quiz.dto;

import com.example.swnuclearfusionwas.domain.quiz.entity.QuestionType;

public record CategoryDto(Long id, String name, String description, QuestionType type) { }
