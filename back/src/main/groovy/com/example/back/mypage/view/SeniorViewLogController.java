package com.example.back.mypage.view;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import com.example.back.domain.User;
import com.example.back.domain.UserRepository;

@RestController
@RequestMapping("/api/views")
public class SeniorViewLogController {

    private final SeniorViewLogService service;
    private final UserRepository userRepository;

    public SeniorViewLogController(SeniorViewLogService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/count")
    public ResponseEntity<ViewCountResponse> getViewCount(
            @RequestParam("caregiverId") Long caregiverId,
            @RequestParam("seniorId") Long seniorId
    ) {
        long count = service.getViewCount(caregiverId, seniorId);
        ViewCountResponse resp = new ViewCountResponse(caregiverId, seniorId, count);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/record")
    public ResponseEntity<Void> recordView(
            @RequestParam("caregiverId") Long caregiverId,
            @RequestParam("seniorId") Long seniorId
    ) {
        service.recordView(caregiverId, seniorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/my-total")
    public ResponseEntity<Map<String, Long>> getMyTotalViewCount() {
        try {
            // 1. (수정) 현재 로그인한 사용자의 *Long ID*를 DB에서 조회
            Long seniorId = getCurrentUserLongId();

            long count = service.getTotalViewCountForSenior(seniorId);
            return ResponseEntity.ok(Map.of("totalViewCount", count));

        } catch (Exception e) {
            // (e.g., 유저를 찾지 못하거나, 인증이 안 된 경우)
            return ResponseEntity.status(401).build();
        }
    }

    // (다른 컨트롤러에서 사용하던 인증 Helper)
    private Long getCurrentUserLongId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("User is not authenticated");
        }

        // 1. Spring Security에서 문자열 ID(e.g., "testid")를 가져옵니다.
        String userIdString = authentication.getName();

        // 2. UserRepository를 사용해 DB에서 User 엔티티를 찾습니다.
        // (findByUserId는 UserRepository에 정의된 메서드여야 합니다)
        User user = userRepository.findByUserId(userIdString)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userIdString));

        // 3. User 엔티티의 실제 Long ID (PK)를 반환합니다.
        return user.getId();
    }
}
