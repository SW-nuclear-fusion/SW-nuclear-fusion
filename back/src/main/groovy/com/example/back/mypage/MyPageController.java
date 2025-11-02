package com.example.back.mypage; // mypage 패키지

import com.example.back.mypage.dto.DailyDataDto;
import com.example.back.mypage.dto.MoodRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage") // 마이페이지 공통 경로
public class MyPageController {

    private final MyPageService myPageService;

    // 월간 달력 데이터
    @GetMapping("/monthly-data")
    public ResponseEntity<List<DailyDataDto>> getMonthlyCalendarData(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        String userId = getCurrentUserId();
        List<DailyDataDto> data = myPageService.getMonthlyData(userId, year, month);
        System.out.println("data : " + data);
        return ResponseEntity.ok(data);
    }

    // 감정 기록 수정
    @PostMapping("/mood")
    public ResponseEntity<Object> saveOrUpdateMood(@Valid @RequestBody MoodRequestDto request) {
        try {
            String userId = getCurrentUserId();
            DailyDataDto result = myPageService.saveOrUpdateDailyMood(userId, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.print("감정 기록 중 오류 발생");
            return ResponseEntity.badRequest().build();
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