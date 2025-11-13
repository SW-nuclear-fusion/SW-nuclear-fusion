package com.example.back.mission; // mission 패키지

import com.example.back.domain.LocationMission;
import com.example.back.domain.MissionLogRepository;
import com.example.back.mission.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    /** 퀴즈: 오늘 풀 퀴즈 5개 가져오기 (GET /api/missions/quiz) */
    @GetMapping("/quiz")
    public ResponseEntity<?> getDailyQuiz() {
        try {
            String userId = getCurrentUserId();
            List<QuizQuestionDto> quiz = missionService.getDailyQuiz(userId);
            return ResponseEntity.ok(quiz);
        } catch (IllegalStateException e) {
            // 이미 퀴즈를 푼 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/quiz/practice")
    public ResponseEntity<?> getPracticeQuiz(@RequestParam("category") String category) {
        try {
            // "연습 퀴즈"는 보상이나 횟수 제한이 없으므로, ID가 필요 없음.
            List<QuizQuestionDto> quiz = missionService.getPracticeQuizByCategory(category);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            // (e.g., category가 잘못된 경우)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/quiz/today")
    public ResponseEntity<?> getTodayQuiz() {
        try {
            String userId = getCurrentUserId();
            // missionService.todayquiz()는 true(완료) 또는 false(미완료)를 반환해야 합니다.
            boolean result = missionService.todayquiz(userId);

            // [개선] 퀴즈 완료 여부(boolean)를 항상 200 OK와 함께 반환합니다.
            return ResponseEntity.ok(result);

        } catch (IllegalStateException e) {
            // [주의] missionService 내부에서 이미 퀴즈가 완료되었을 때 IllegalStateException을 던진다면,
            // 이는 이미 result=true로 처리되어야 하므로 이 catch 블록은 사실상 필요하지 않습니다.
            // 만약 이 예외가 발생한다면, 이는 서버 로직 오류로 간주하여 아래 Exception에서 처리하거나,
            // MissionService 내에서 예외 대신 boolean을 반환하도록 수정해야 합니다.

            // 현재 로직을 유지한다면: (추천하지 않음)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // [개선] 서버 내부 오류는 500, 인증/입력 오류는 400을 사용하는 것이 좋습니다.
            // 여기서는 일반적인 오류로 400을 반환하도록 유지합니다.
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** 퀴즈: 답안 제출 (POST /api/missions/quiz/submit) */
    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody List<QuizAnswerDto> answers) {
        try {
            String userId = getCurrentUserId();
            QuizResultDto result = missionService.submitQuiz(userId, answers);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            // 이미 제출한 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** 퀴즈: 답안 제출 (POST /api/missions/quiz/submit) */
    @PostMapping("/quiz/practice/submit")
    public ResponseEntity<?> submitpracticeQuiz(@RequestBody List<QuizAnswerDto> answers) {
        try {
            String userId = getCurrentUserId();
            QuizResultDto result = missionService.submitpracticeQuiz(userId, answers);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            // 이미 제출한 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** 방문: 모든 방문 미션 목록 조회 (GET /api/missions/locations) */
    @GetMapping("/locations")
    public ResponseEntity<List<LocationMission>> getAllLocationMissions() {
        List<LocationMission> datas = missionService.getAllLocationMissions();

        return ResponseEntity.ok(datas);
    }

    /** 방문: 방문 인증 시도 (POST /api/missions/visit/{missionId}) */
    @PostMapping("/visit/{missionId}")
    public ResponseEntity<?> attemptVisitMission(
            @PathVariable("missionId") Long missionId,
            @RequestBody VisitRequestDto visitRequest) {
        try {
            String userId = getCurrentUserId();
            RewardDto reward = missionService.completeVisitMission(userId, missionId, visitRequest);
            // 성공 시 보상 정보 반환
            return ResponseEntity.ok(reward);
        } catch (IllegalStateException e) {
            // 이미 완료한 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 거리가 멀거나 ID가 잘못된 경우
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
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