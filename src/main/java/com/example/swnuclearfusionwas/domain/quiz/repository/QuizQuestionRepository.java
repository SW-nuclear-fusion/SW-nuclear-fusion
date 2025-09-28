package com.example.swnuclearfusionwas.domain.quiz.repository;

import com.example.swnuclearfusionwas.domain.quiz.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByCategoryIdAndDifficultyAndActiveTrue(Long categoryId, Difficulty difficulty, Pageable pageable);
}
