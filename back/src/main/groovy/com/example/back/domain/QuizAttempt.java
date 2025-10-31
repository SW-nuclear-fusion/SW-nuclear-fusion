package com.example.back.domain; // 패키지 확인

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "quiz_attempts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "attempt_date"})
        })
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "attempt_date", nullable = false, updatable = false)
    private LocalDate attemptDate;

    @Column(nullable = false)
    private int correctCount; // 맞춘 개수

    @Column(nullable = false)
    private int totalCount; // 전체 문제 개수

    @Builder
    public QuizAttempt(User user, int correctCount, int totalCount) {
        this.user = user;
        this.correctCount = correctCount;
        this.totalCount = totalCount;
    }
}