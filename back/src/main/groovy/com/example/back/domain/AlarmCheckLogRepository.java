package com.example.back.domain; // 패키지 확인

import com.example.back.mypage.dto.MedicationCheckDto; // [!] DTO 경로 (다음 단계에서 생성)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AlarmCheckLogRepository extends JpaRepository<AlarmCheckLog, Long> {

    boolean existsByUser_UserIdAndAlarm_IdAndCheckDate(String userId, Long alarmId, LocalDate checkDate);
    @Query("SELECT acl.alarm.id FROM AlarmCheckLog acl WHERE acl.user.id = :userId AND acl.checkDate = :date")
    Set<Long> findCheckedAlarmIdsByUserIdAndCheckDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("SELECT new com.example.back.mypage.dto.MedicationCheckDto(acl.checkDate, a.medicationName) " +
            "FROM AlarmCheckLog acl JOIN acl.alarm a " +
            "WHERE acl.user.userId = :userId AND acl.checkDate BETWEEN :startDate AND :endDate")
    List<MedicationCheckDto> findMedicationLogsByDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}