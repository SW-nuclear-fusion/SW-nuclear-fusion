package com.example.back.domain; // 패키지 확인

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional; // Optional import

public interface AlarmRepository extends JpaRepository<Alarm, Long> { // <엔티티, ID타입>

    // 특정 사용자의 모든 알람을 시간순으로 조회
    List<Alarm> findByUser_UserIdOrderByNotificationTimeAsc(String userId);

    // 특정 사용자의 특정 알람 ID로 조회 (본인 확인용)
    Optional<Alarm> findByIdAndUser_UserId(Long alarmId, String userId);
}