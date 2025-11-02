package com.example.back.alarm; // alarm 패키지 확인

import com.example.back.alarm.dto.AlarmDto;
import com.example.back.domain.Alarm;
import com.example.back.domain.AlarmRepository;
import com.example.back.domain.User;
import com.example.back.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException; // [!] 추가
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Optional import
import com.example.back.domain.AlarmCheckLogRepository; // [!] AlarmCheckLogRepository import
import java.time.LocalDate; // [!] LocalDate import
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AlarmCheckLogRepository alarmCheckLogRepository;
    /** 사용자의 모든 알람 조회 */
    public List<AlarmDto> getAlarms(String userId) {
        List<Alarm> alarms = alarmRepository.findByUser_UserIdOrderByNotificationTimeAsc(userId);
        return AlarmDto.fromEntities(alarms);
    }

    /** 여러 개의 새 알람 저장 */
    @Transactional // 쓰기 작업이므로 @Transactional 명시
    public List<AlarmDto> addAlarms(String userId, List<AlarmDto> alarmDtos) {
        User user = findUserByUserId(userId);
        List<Alarm> savedAlarms = new ArrayList<>();

        for (AlarmDto dto : alarmDtos) {
            Alarm alarm = Alarm.builder()
                    .user(user)
                    .medicationName(dto.getMedicationName())
                    .notificationTime(LocalTime.parse(dto.getNotificationTime(), DateTimeFormatter.ISO_LOCAL_TIME))
                    .notificationDays(dto.getNotificationDays())
                    .enabled(dto.getEnabled() != null ? dto.getEnabled() : true)
                    .build();
            savedAlarms.add(alarmRepository.save(alarm));
        }
        return AlarmDto.fromEntities(savedAlarms);
    }

    /** 알람 활성화/비활성화 토글 */
    @Transactional
    public AlarmDto toggleAlarm(String userId, Long alarmId) {
        Alarm alarm = findAlarmByIdAndUserId(alarmId, userId); // 본인 알람인지 확인
        alarm.setEnabled(!alarm.isEnabled());
        return new AlarmDto(alarm);
    }

    /** 알람 삭제 */
    @Transactional
    public void deleteAlarm(String userId, Long alarmId) {
        Alarm alarm = findAlarmByIdAndUserId(alarmId, userId);
        alarmRepository.delete(alarm);
    }

    public Set<Long> getCheckedAlarmIdsForDate(String userId, LocalDate date) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Long numericUserId = user.getId();

        return alarmCheckLogRepository.findCheckedAlarmIdsByUserIdAndCheckDate(numericUserId, date);
    }

    // --- Helper Methods ---
    private User findUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
    }

    private Alarm findAlarmByIdAndUserId(Long alarmId, String userId) {
        return alarmRepository.findByIdAndUser_UserId(alarmId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Alarm not found or permission denied: " + alarmId));
    }
}