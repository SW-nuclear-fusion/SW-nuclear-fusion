package com.example.back.user;

import com.example.back.domain.User;
import com.example.back.domain.UserRepository;
import com.example.back.user.dto.UserInfoResponse;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.back.user.dto.PlantActivateRequest;
import jakarta.validation.Valid;
import com.example.back.user.dto.RewardExchangeRequest;
import com.example.back.user.dto.RewardExchangeResponse;
import com.example.back.user.dto.RewardVoucherListResponse;

// [신규] 3개 DTO 및 List 임포트
import com.example.back.user.dto.UserPlantDto;
import com.example.back.user.dto.RewardVoucherDto;
import com.example.back.user.dto.UserUpdateRequest;
import java.util.List;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // --- (기존 API: 식물 활성화) ---
    // (내부 로직은 Service에서 바뀌었지만, 컨트롤러는 동일함)
    @PostMapping("/plant/activate")
    public ResponseEntity<?> activatePlant(
            @Valid @RequestBody PlantActivateRequest request) {

        String currentUserId = getCurrentUserId();
        try {
            UserInfoResponse updatedUserInfo = userService.activatePlant(currentUserId, request);
            return ResponseEntity.ok(updatedUserInfo);
        } catch (EntityNotFoundException e) { // 404 (식물 못 찾음)
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) { // 400 (규칙 위반: 만렙 아님 등)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) { // 500
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // --- (기존 API: 내 정보 조회) ---
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo() {
        String currentUserId = getCurrentUserId();
        UserInfoResponse userInfo = userService.getCurrentUserInfo(currentUserId);
        return ResponseEntity.ok(userInfo);
    }

    // --- [신규 API 1] GET /api/user/plants (보유 식물 4개 전체 조회) ---
    @GetMapping("/plants")
    public ResponseEntity<List<UserPlantDto>> getMyAllPlants() {
        String currentUserId = getCurrentUserId();
        List<UserPlantDto> plants = userService.getAllUserPlants(currentUserId);
        return ResponseEntity.ok(plants);
    }

    @PostMapping("/rewards/exchange")
    public ResponseEntity<?> exchangeReward(
            @Valid @RequestBody RewardExchangeRequest request) {

        String currentUserId = getCurrentUserId();
        try {
            RewardExchangeResponse response = userService.exchangeReward(currentUserId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // 포인트 부족 등
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // --- [신규 API 2] GET /api/user/rewards/vouchers (교환권 목록 조회) ---
    @GetMapping("/rewards/vouchers")
    public ResponseEntity<?> getRewardVouchers() {
        String currentUserId = getCurrentUserId();
        try {
            RewardVoucherListResponse response = userService.getVouchersByStatus(currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    // --- [신규 API 2] GET /api/user/rewards (보상 교환권 전체 조회) ---
    @GetMapping("/rewards")
    public ResponseEntity<List<RewardVoucherDto>> getMyAllRewards() {
        String currentUserId = getCurrentUserId();
        List<RewardVoucherDto> vouchers = userService.getAllUserVouchers(currentUserId);
        return ResponseEntity.ok(vouchers);
    }

    // --- [신규 API 3] PATCH /api/user/me (사용자 정보 변경) ---
    @PatchMapping("/me")
    public ResponseEntity<UserInfoResponse> updateMyInfo(
            @Valid @RequestBody UserUpdateRequest request) {

        String currentUserId = getCurrentUserId();
        UserInfoResponse updatedUserInfo = userService.updateUserInfo(currentUserId, request);
        return ResponseEntity.ok(updatedUserInfo);
    }

    // --- (이하 기존 API: 물 주기, 애정 주기, 알람 체크, 뷰 카운트) ---
    @PostMapping("/plant/water")
    public ResponseEntity<?> waterPlant() {
        String currentUserId = getCurrentUserId();
        try {
            UserInfoResponse updatedUserInfo = userService.waterPlant(currentUserId);
            return ResponseEntity.ok(updatedUserInfo);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/plant/affection")
    public ResponseEntity<?> giveAffection() {
        String currentUserId = getCurrentUserId();
        try {
            UserInfoResponse updatedUserInfo = userService.giveAffection(currentUserId);
            return ResponseEntity.ok(updatedUserInfo);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // --- [신규 API] 만렙 포인트 수령 ---
    @PostMapping("/plant/claim-points")
    public ResponseEntity<?> claimPoints() {
        String currentUserId = getCurrentUserId();
        try {
            // (1000 포인트 지급 및 상태 변경)
            UserInfoResponse updatedUserInfo = userService.claimPlantPoints(currentUserId);
            return ResponseEntity.ok(updatedUserInfo); // 최신 유저 정보 반환
        } catch (IllegalStateException e) {
            // (예: "아직 만렙 아님", "이미 수령함")
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/alarms/{alarmId}/check")
    public ResponseEntity<Object> checkOffAlarm(@PathVariable("alarmId") Long alarmId) {
        String currentUserId = getCurrentUserId();
        try {
            UserInfoResponse updatedUserInfo = userService.checkOffAlarm(currentUserId, alarmId);
            return ResponseEntity.ok(updatedUserInfo);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/view-count")
    public ResponseEntity<Map<String, Integer>> getMyTotalViewCount(
            @AuthenticationPrincipal String userId
    ) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return ResponseEntity.ok(Map.of("totalViewCount", user.getGuardianViewCount()));
    }

    // --- (Helper Method) ---
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("User is not authenticated");
        }
        return authentication.getName();
    }
}