package com.example.swnuclearfusionwas.domain.meds.controller;

import com.example.swnuclearfusionwas.domain.meds.dto.*;
import com.example.swnuclearfusionwas.domain.meds.service.AlertService;
import com.example.swnuclearfusionwas.domain.meds.service.MedService;
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

import java.net.URI;
import java.time.ZoneId;
import java.util.Map;

@Tag(name = "복용약 관리 API", description = "복용약 등록, 조회, 수정, 삭제 및 알림/복용 이벤트 관리 API")
@RestController
@RequestMapping("/api/meds")
@RequiredArgsConstructor
public class MedController {

    private final MedService medService;
    private final AlertService alertService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepo;

    private Long currentUserId(HttpServletRequest req) {
        Cookie[] cs = req.getCookies(); if (cs==null) return null;
        String token=null; for (Cookie c: cs) if ("Authorization".equals(c.getName())) token=c.getValue();
        if (token==null || Boolean.TRUE.equals(jwtUtil.isExpired(token))) return null;
        Long userId = jwtUtil.parseUserId(token);

        return userRepo.findById(userId)
                .map(UserEntity::getId)
                .orElse(null);
    }

    @Operation(
        summary = "복용약 등록",
        description = "신규 복용약을 등록합니다.\n\n- 하루 복용 횟수, 복용 시간, 요일 등 다양한 옵션을 입력할 수 있습니다.\n- 등록 후 medId가 반환됩니다.",
        tags = {"복용약 관리 API"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "복용약 등록 요청 DTO 예시",
            required = true,
            content = @Content(
                schema = @Schema(implementation = MedCreateReq.class),
                examples = @ExampleObject(
                    value = "{\n  \"name\": \"타이레놀\",\n  \"frequencyPerDay\": 3,\n  \"times\": [\"08:00\", \"13:00\", \"20:00\"],\n  \"everyDay\": true,\n  \"daysOfWeek\": [\"MONDAY\", \"WEDNESDAY\"]\n}"
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"medId\": 1}"))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody MedCreateReq req,
            HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        Long id = medService.create(uid, req);
        return ResponseEntity.created(URI.create("/api/meds/medId="+id)).body(Map.of("medId", id));
    }

    @Operation(
        summary = "복용약 목록 조회",
        description = "현재 로그인한 사용자의 모든 복용약 목록을 조회합니다.\n\n- 각 복용약의 medId, 이름, 복용 횟수, 알림 여부, 스케줄 정보가 포함됩니다.",
        tags = {"복용약 관리 API"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedListItemDto.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @GetMapping
    public ResponseEntity<?> list(HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        return ResponseEntity.ok(medService.list(uid));
    }

    @Operation(
        summary = "복용약 상세 조회",
        description = "특정 복용약(medId)의 상세 정보를 조회합니다.\n\n- medId, 이름, 복용 횟수, 알림 여부, 스케줄 정보가 포함됩니다.",
        tags = {"복용약 관리 API"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedDetailDto.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @GetMapping("/medId={medId}")
    public ResponseEntity<?> detail(
            @Parameter(description = "복용약 고유 ID", example = "1", required = true) @PathVariable Long medId,
            HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        return ResponseEntity.ok(medService.detail(uid, medId));
    }

    @Operation(
        summary = "복용약 정보 수정",
        description = "복용약의 이름, 복용 횟수, 시간, 요일 등 정보를 수정합니다.\n\n- 수정 후 medId와 updated=true가 반환됩니다.",
        tags = {"복용약 관리 API"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "복용약 수정 요청 DTO 예시",
            required = true,
            content = @Content(
                schema = @Schema(implementation = MedUpdateReq.class),
                examples = @ExampleObject(
                    value = "{\n  \"name\": \"타이레놀\",\n  \"frequencyPerDay\": 2,\n  \"times\": [\"09:00\", \"21:00\"],\n  \"everyDay\": false,\n  \"daysOfWeek\": [\"TUESDAY\", \"THURSDAY\"]\n}"
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"medId\": 1, \"updated\": true}"))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @PatchMapping("/medId={medId}")
    public ResponseEntity<?> update(
            @Parameter(description = "복용약 고유 ID", example = "1", required = true) @PathVariable Long medId,
            @RequestBody MedUpdateReq req,
            HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        medService.update(uid, medId, req);
        return ResponseEntity.ok(Map.of("medId", medId, "updated", true));
    }

    @Operation(
        summary = "복용약 삭제",
        description = "특정 복용약(medId)을 삭제합니다.\n\n- 삭제 후 deleted=true가 반환됩니다.",
        tags = {"복용약 관리 API"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"deleted\": true}"))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @DeleteMapping("/medId={medId}")
    public ResponseEntity<?> delete(
            @Parameter(description = "복용약 고유 ID", example = "1", required = true) @PathVariable Long medId,
            HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        medService.delete(uid, medId);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @Operation(
        summary = "알림 토글",
        description = "복용약의 알림을 활성화/비활성화합니다.\n\n- medId, alertEnabled가 반환됩니다.",
        tags = {"복용약 관리 API"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "알림 토글 요청 DTO 예시",
            required = true,
            content = @Content(
                schema = @Schema(implementation = AlertToggleReq.class),
                examples = @ExampleObject(
                    value = "{\n  \"medId\": 1,\n  \"enabled\": false\n}"
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토글 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"medId\": 1, \"alertEnabled\": false}"))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @PatchMapping("/alert/toggle")
    public ResponseEntity<?> toggle(
            @RequestBody AlertToggleReq req,
            HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        medService.toggleAlert(uid, req.getMedId(), Boolean.TRUE.equals(req.getEnabled()));
        return ResponseEntity.ok(Map.of("medId", req.getMedId(), "alertEnabled", req.getEnabled()));
    }

    @Operation(
        summary = "복용 완료 처리",
        description = "복용 완료 시 이벤트를 처리합니다.\n\n- eventId, taken=true가 반환됩니다.",
        tags = {"복용약 관리 API"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "복용 완료 요청 DTO 예시",
            required = true,
            content = @Content(
                schema = @Schema(implementation = AlertCompleteReq.class),
                examples = @ExampleObject(
                    value = "{\n  \"eventId\": 10\n}"
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "복용 완료 처리 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"eventId\": 10, \"taken\": true}"))),
        @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"unauthorized\"}")))
    })
    @PostMapping("/alert/complete")
    public ResponseEntity<?> complete(
            @RequestBody AlertCompleteReq req,
            HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        alertService.complete(uid, req.getEventId());
        return ResponseEntity.ok(Map.of("eventId", req.getEventId(), "taken", true));
    }


}