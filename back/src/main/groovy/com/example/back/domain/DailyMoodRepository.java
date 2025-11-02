package com.example.back.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMoodRepository extends JpaRepository<DailyMood, Long> {

    // 특정 날짜의 감정 기록 조회 (수정용)
    Optional<DailyMood> findByUser_UserIdAndMoodDate(String userId, LocalDate moodDate);

    // 특정 기간의 감정 기록 목록 조회 (달력용)
    List<DailyMood> findAllByUser_UserIdAndMoodDateBetween(String userId, LocalDate startDate, LocalDate endDate);
}