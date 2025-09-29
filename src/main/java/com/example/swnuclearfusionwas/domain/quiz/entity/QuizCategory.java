package com.example.swnuclearfusionwas.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor
@Table(name = "quiz_category")
public class QuizCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String name;

    @Column(length=255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length=10)
    private QuestionType type = QuestionType.MCQ;

}
