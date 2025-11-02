package com.example.back.user;

import com.example.back.domain.User; // User import
import com.example.back.user.dto.PlantSelectionRequest;
import com.example.back.user.dto.UserInfoResponse;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // [!] Authentication import
import org.springframework.security.core.context.SecurityContextHolder; // [!] SecurityContextHolder import
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user") // 사용자 관련 API 공통 경로
public class UserController {

    private final UserService userService;

    /**
     * 식물 선택 정보 저장 (POST /api/user/plant)
     */
    @PostMapping("/plant")
    public ResponseEntity<Map<String, String>> selectPlant(
            @Valid @RequestBody PlantSelectionRequest request) {

        // [!] 현재 로그인된 사용자의 ID 가져오기 (JWT 필터에서 설정됨)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName(); // JwtAuthenticationFilter에서 userId를 name으로 설정했음

        userService.savePlantSelection(currentUserId, request);
        return ResponseEntity.ok(Map.of("message", "식물 정보가 저장되었습니다."));
    }

    /**
     * 내 정보 조회 (GET /api/user/me)
     * 홈 화면 등에서 사용
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo() {
        String currentUserId = getCurrentUserId();
        // [!] Service가 UserInfoResponse를 반환하는지 확인!
        UserInfoResponse userInfo = userService.getCurrentUserInfo(currentUserId);
        return ResponseEntity.ok(userInfo); // DTO 객체 반환
    }

    /** [!] 물 주기 API (POST /api/user/plant/water) */
    @PostMapping("/plant/water")
    public ResponseEntity<?> waterPlant() {
        String currentUserId = getCurrentUserId();
        try {
            UserInfoResponse updatedUserInfo = userService.waterPlant(currentUserId);
            return ResponseEntity.ok(updatedUserInfo); // 업데이트된 정보 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** [!] 애정 주기 API (POST /api/user/plant/affection) */
    @PostMapping("/plant/affection")
    public ResponseEntity<?> giveAffection() {
        String currentUserId = getCurrentUserId();
        try {
            UserInfoResponse updatedUserInfo = userService.giveAffection(currentUserId);
            return ResponseEntity.ok(updatedUserInfo); // 업데이트된 정보 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 알람 체크(복용 완료) API
     */
    @PostMapping("/alarms/{alarmId}/check")
    public ResponseEntity<Object> checkOffAlarm(@PathVariable("alarmId") Long alarmId) { // 반환 타입 Object로 변경 권장
        String currentUserId = getCurrentUserId();
        try {
            // UserService의 checkOffAlarm 호출
            UserInfoResponse updatedUserInfo = userService.checkOffAlarm(currentUserId, alarmId);
            // 성공 시 업데이트된 사용자 정보 반환
            return ResponseEntity.ok(updatedUserInfo);
        } catch (EntityNotFoundException e) { // 알람 ID 못 찾거나 권한 없을 때
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) { // 그 외 예외 (예: 보상 계산 오류 등)
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // --- Helper Method ---
    /** 현재 로그인된 사용자 ID 가져오는 공통 메서드 */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("User is not authenticated"); // 혹은 다른 예외 처리
        }
        return authentication.getName();
    }
}
