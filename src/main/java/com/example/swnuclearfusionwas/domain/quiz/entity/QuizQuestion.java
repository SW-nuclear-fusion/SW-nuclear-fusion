package com.example.swnuclearfusionwas.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity @Getter @Setter @NoArgsConstructor
@Table(name = "quiz_question")
public class QuizQuestion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private QuizCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Difficulty difficulty;

    // MCQ/메모리 구분(이 엔티티는 MCQ만 사용)
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private QuestionType type = QuestionType.MCQ;

    @Column(nullable=false, length=1000)
    private String questionText;

    @ElementCollection
    @CollectionTable(name="quiz_question_choice", joinColumns=@JoinColumn(name="question_id"))
    @Column(name="choice_text", length=500)
    private List<String> choices;

    @Column(nullable=false)
    private int correctIndex;

    @Column(length=1000)
    private String explanation;

    // 운영 편의
    private String source;
    private String tags;

    // repo에서 active만 가져오도록 할거라 기본 true
    @Column(nullable=false)
    private Boolean active = true;
}
