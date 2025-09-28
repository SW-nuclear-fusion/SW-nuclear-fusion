package com.example.swnuclearfusionwas.domain.quiz.service;

import com.example.swnuclearfusionwas.domain.quiz.dto.*;
import com.example.swnuclearfusionwas.domain.quiz.entity.*;
import com.example.swnuclearfusionwas.domain.quiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizCategoryRepository categoryRepo;
    private final QuizQuestionRepository questionRepo;

    @Override
    public List<CategoryDto> getCategories() {
        return categoryRepo.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription(), c.getType()))
                .toList();
    }

    @Override
    public List<QuestionDto> getMcqQuestions(Long categoryId, Difficulty difficulty, int limit) {
        return questionRepo
                .findByCategoryIdAndDifficultyAndActiveTrue(categoryId, difficulty, PageRequest.of(0, limit))
                .stream()
                .map(q -> new QuestionDto(q.getId(), q.getQuestionText(), q.getChoices()))
                .toList();
    }

    @Override
    public SubmitResult submitMcq(SubmitPayload payload) {
        Map<Long, QuizQuestion> byId = new HashMap<>();
        payload.answers().stream().map(SubmitPayload.Answer::questionId).distinct().forEach(id ->
                questionRepo.findById(id).ifPresent(q -> byId.put(id, q))
        );

        int total = payload.answers().size();
        int correct = 0;
        List<Long> wrong = new ArrayList<>();

        for (var a : payload.answers()) {
            var q = byId.get(a.questionId());
            if (q == null || a.choiceIndex() == null) { wrong.add(a.questionId()); continue; }
            if (q.getCorrectIndex() == a.choiceIndex()) correct++;
            else wrong.add(a.questionId());
        }
        int reward = correct * 10; // 필요시 난이도 가중치 반영
        return new SubmitResult(total, correct, wrong, reward);
    }
}
