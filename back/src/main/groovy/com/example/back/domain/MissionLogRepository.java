package com.example.back.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface MissionLogRepository extends JpaRepository<MissionLog, Long> {
    // 오늘 날짜로 해당 미션을 완료한 기록이 있는지 확인
    boolean existsByUser_UserIdAndMission_IdAndCompletionDate(String userId, Long missionId, LocalDate completionDate);
}