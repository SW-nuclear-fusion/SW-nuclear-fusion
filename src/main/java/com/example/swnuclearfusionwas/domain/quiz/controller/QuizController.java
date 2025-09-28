package com.example.swnuclearfusionwas.domain.quiz.controller;

import com.example.swnuclearfusionwas.domain.quiz.dto.*;
import com.example.swnuclearfusionwas.domain.quiz.entity.Difficulty;
import com.example.swnuclearfusionwas.domain.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/categories")
    public List<CategoryDto> categories() {
        return quizService.getCategories();
    }

    @GetMapping("/questions")
    public List<QuestionDto> mcq(@RequestParam Long categoryId,
                                 @RequestParam Difficulty difficulty,
                                 @RequestParam(defaultValue = "10") int limit) {
        return quizService.getMcqQuestions(categoryId, difficulty, limit);
    }

    @PostMapping("/submit")
    public SubmitResult submit(@RequestBody SubmitPayload payload) {
        return quizService.submitMcq(payload);
    }
}
