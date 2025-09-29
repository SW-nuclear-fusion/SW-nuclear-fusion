package com.example.swnuclearfusionwas.domain.quiz.service;

import com.example.swnuclearfusionwas.domain.quiz.dto.*;
import com.example.swnuclearfusionwas.domain.quiz.entity.Difficulty;
import java.util.List;

public interface QuizService {
    List<CategoryDto> getCategories();                                  // MEMORY + MCQ 모두
    List<QuestionDto> getMcqQuestions(Long categoryId, Difficulty difficulty, int limit);
    SubmitResult submitMcq(SubmitPayload payload);
}
