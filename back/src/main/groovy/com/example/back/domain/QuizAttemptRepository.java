package com.example.back.domain; // 패키지 확인

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List; // [!] List import

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // 오늘 퀴즈 풀었는지 확인 (기존)
    boolean existsByUser_UserIdAndAttemptDate(String userId, LocalDate attemptDate);

    List<QuizAttempt> findAllByUser_UserIdAndAttemptDateBetween(String userId, LocalDate startDate, LocalDate endDate);
}