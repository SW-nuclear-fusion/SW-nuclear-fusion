package com.example.back.alarm; // alarm 패키지 확인

import com.example.back.alarm.dto.AlarmDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat; // [!] 날짜 파싱용 import
import java.time.LocalDate; // [!] LocalDate import
import java.util.Set;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/alarms") // 알람 관련 API 공통 경로: /api/user/alarms
public class AlarmController {

    private final AlarmService alarmService;

    /** 내 알람 목록 조회 */
    @GetMapping
    public ResponseEntity<List<AlarmDto>> getMyAlarms() {
        String currentUserId = getCurrentUserId();
        List<AlarmDto> alarms = alarmService.getAlarms(currentUserId);
        return ResponseEntity.ok(alarms);
    }

    /** 새 알람 추가 */
    @PostMapping
    public ResponseEntity<List<AlarmDto>> addAlarms(@Valid @RequestBody List<AlarmDto> alarmDtos) {
        String currentUserId = getCurrentUserId();
        try {
            List<AlarmDto> addedAlarms = alarmService.addAlarms(currentUserId, alarmDtos);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedAlarms);
        } catch (Exception e) {
            // 예외 처리 (예: 시간 형식 오류 등)
            // 실제로는 @ControllerAdvice 등으로 전역 처리하는 것이 좋음
            return ResponseEntity.badRequest().build();
        }
    }

    /** 특정 알람 토글 */
    @PatchMapping("/{alarmId}/toggle")
    public ResponseEntity<AlarmDto> toggleAlarm(@PathVariable Long alarmId) {
        String currentUserId = getCurrentUserId();
        try {
            AlarmDto toggledAlarm = alarmService.toggleAlarm(currentUserId, alarmId);
            return ResponseEntity.ok(toggledAlarm);
        } catch (EntityNotFoundException e) { // Service에서 발생시킨 예외
            return ResponseEntity.notFound().build();
        }
    }

    /** 특정 알람 삭제 */
    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Map<String, String>> deleteAlarm(@PathVariable Long alarmId) {
        String currentUserId = getCurrentUserId();
        try {
            alarmService.deleteAlarm(currentUserId, alarmId);
            return ResponseEntity.ok(Map.of("message", "알람이 삭제되었습니다."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/checked")
    public ResponseEntity<Set<Long>> getCheckedAlarmsForDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String currentUserId = getCurrentUserId();
        System.out.println(currentUserId);
        try {
            Set<Long> checkedIds = alarmService.getCheckedAlarmIdsForDate(currentUserId, date);
            return ResponseEntity.ok(checkedIds);
        } catch (Exception e) {
            System.err.println("Error fetching checked alarms: " + e.getMessage()); // 임시 로그
            return ResponseEntity.internalServerError().build(); // 또는 다른 에러 응답
        }
    }

    // --- Helper Method ---
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("User is not authenticated");
        }
        return authentication.getName();
    }
}