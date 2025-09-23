package com.example.swnuclearfusionwas.domain.meds.controller;

import com.example.swnuclearfusionwas.domain.meds.dto.PushSubscriptionReq;
import com.example.swnuclearfusionwas.domain.meds.entity.PushSubscription;
import com.example.swnuclearfusionwas.domain.meds.repository.PushSubscriptionRepository;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTUtil;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.swnuclearfusionwas.domain.meds.service.PushService;

import java.util.Map;

@Tag(name = "웹 푸시 구독/키 관리 API", description = "웹 푸시 구독 등록, 공개키 제공 등 푸시 관련 API")
@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushController {
    private final PushSubscriptionRepository pushRepo;
    private final PushService pushService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepo;

    private Long currentUserId(HttpServletRequest req) {
        Cookie[] cs = req.getCookies(); if (cs==null) return null;
        String token=null; for (Cookie c: cs) if ("Authorization".equals(c.getName())) token=c.getValue();
        if (token==null || Boolean.TRUE.equals(jwtUtil.isExpired(token))) return null;
        Long userId = jwtUtil.parseUserId(token);
        return userRepo.findById(userId)
                .map(UserEntity::getId)
                .orElse(null);    }

    @Operation(
        summary = "웹 푸시 공개키 조회",
        description = "웹 푸시 구독에 필요한 VAPID 공개키를 반환합니다.\n\n- 클라이언트는 이 키로 브라우저에서 푸시 구독을 생성할 수 있습니다.",
        tags = {"웹 푸시 구독/키 관리 API"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "공개키 반환 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"publicKey\": \"BEx...==\"}")))
    })
    @GetMapping("/public-key")
    public ResponseEntity<?> publicKey() {
        return ResponseEntity.ok(Map.of("publicKey", pushService.getPublicKey()));
    }

    @Operation(
        summary = "웹 푸시 구독 등록",
        description = "브라우저에서 발급받은 endpoint, p256dh, auth 정보를 서버에 등록합니다.\n\n- 최초 등록 시에만 DB에 저장됩니다.\n- 이미 등록된 endpoint는 중복 저장되지 않습니다.",
        tags = {"웹 푸시 구독/키 관리 API"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "웹 푸시 구독 등록 요청 DTO 예시",
            required = true,
            content = @Content(
                schema = @Schema(implementation = PushSubscriptionReq.class),
                examples = @ExampleObject(
                    value = "{\n  \"endpoint\": \"https://fcm.googleapis.com/fcm/send/abc123...\",\n  \"p256dh\": \"BEx...==\",\n  \"auth\": \"abc123==\"\n}"
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구독 등록 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "subscribed"))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @RequestBody PushSubscriptionReq req,
            HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");

        boolean exists = pushRepo.findByUserIdAndEndpoint(userId, req.getEndpoint()).isPresent();
        if (!exists) {
            PushSubscription sub = PushSubscription.builder()
                    .userId(userId)
                    .endpoint(req.getEndpoint())
                    .p256dh(req.getP256dh())
                    .auth(req.getAuth())
                    .build();
            pushRepo.save(sub);
        }
        return ResponseEntity.ok().body("subscribed");
    }
}