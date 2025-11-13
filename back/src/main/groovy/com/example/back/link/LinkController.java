package com.example.back.link;

import com.example.back.link.dto.*;
import com.example.back.link.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.back.mypage.dto.DailyDataDto;
import com.example.back.link.dto.RejectedLinkDto;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/link")
public class LinkController {

    private final LinkService linkService;

    /**
     * 보호자가 시니어에게 연동 요청
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestLink(
            @AuthenticationPrincipal String userId, // 현재 로그인한 사용자의 ID
            @Valid @RequestBody LinkRequestDto requestDto
    ) {
        linkService.requestLink(userId, requestDto);

        // (참고: 공통 응답 DTO를 만들어 success: true, message: "..." 형태로 반환하는 것이 더 좋습니다)
        return ResponseEntity.ok("연결 요청을 성공적으로 보냈습니다.");
    }

    // --- [!] 시니어 기능 1: 나에게 온 요청 목록 조회 ---
    /**
     * (시니어) 본인에게 온 'PENDING' 상태의 연결 요청 목록 조회
     */
    @GetMapping("/requests")
    public ResponseEntity<List<LinkPendingResponseDto>> getPendingRequests(
            @AuthenticationPrincipal String userId // 현재 로그인한 시니어의 ID
    ) {
        List<LinkPendingResponseDto> pendingRequests = linkService.getPendingRequests(userId);
        return ResponseEntity.ok(pendingRequests);
    }

    // --- [!] 시니어 기능 2: 연결 요청 승인 또는 거절 ---
    /**
     * (시니어) 본인에게 온 연결 요청 응답 (승인/거절)
     */
    @PostMapping("/respond")
    public ResponseEntity<String> respondToLink(
            @AuthenticationPrincipal String userId, // 현재 로그인한 시니어의 ID
            @Valid @RequestBody LinkRespondDto respondDto
    ) {
        linkService.respondToLinkRequest(userId, respondDto);
        return ResponseEntity.ok("요청이 처리되었습니다.");
    }

    /**
     * (보호자) 특정 시니어의 상세 상태 조회 (마이페이지 열람)
     */
    @GetMapping("/senior-status/{seniorId}")
    public ResponseEntity<SeniorStatusDto> getSeniorStatus(
            @AuthenticationPrincipal String userId, // (보호자)
            @PathVariable("seniorId") Long seniorId
    ) {
        SeniorStatusDto seniorStatus = linkService.getSeniorStatus(userId, seniorId);
        return ResponseEntity.ok(seniorStatus);
    }

    // --- [!] 보호자 기능: 연결된 시니어 목록 조회 (신규 추가) ---

    /**
     * (보호자) 본인이 연결(승인)한 시니어 목록 전체 조회
     */
    @GetMapping("/seniors")
    public ResponseEntity<List<LinkedSeniorDto>> getMyLinkedSeniors(
            @AuthenticationPrincipal String userId // (보호자)
    ) {
        List<LinkedSeniorDto> seniors = linkService.getApprovedSeniors(userId);
        return ResponseEntity.ok(seniors);
    }

    @GetMapping("/requests/rejected")
    public ResponseEntity<List<RejectedLinkDto>> getMyRejectedRequests(
            @AuthenticationPrincipal String userId // (보호자)
    ) {
        List<RejectedLinkDto> requests = linkService.getRejectedRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/senior-monthly/{seniorId}")
    public ResponseEntity<List<DailyDataDto>> getSeniorMonthlyData(
            @AuthenticationPrincipal String userId, // (보호자)
            @PathVariable("seniorId") Long seniorId,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        List<DailyDataDto> monthlyData = linkService.getSeniorMonthlyData(userId, seniorId, year, month);
        return ResponseEntity.ok(monthlyData);
    }
}