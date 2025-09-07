package com.example.swnuclearfusionwas.domain.mypage.controller;

import com.example.swnuclearfusionwas.domain.mypage.dto.CreateLinkReq;
import com.example.swnuclearfusionwas.domain.mypage.service.LinkReqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Link", description = "시니어/보호자 연결 요청 API")
@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkReqController {

    private final LinkReqService linkRequestService;

    @Operation(
            summary = "연결 요청 생성(보호자 → 시니어)",
            description = """
            로그인한 **보호자**가 상대방(시니어)의 전화번호로 연결 요청을 생성합니다.
            
            권한/검증 규칙
            - 요청자는 반드시 **보호자**여야 합니다. (보호자가 아니면 403)
            - 수신자는 반드시 **시니어**여야 합니다.
            - 본인에게는 요청할 수 없습니다.
            - 이미 동일 쌍(요청자-수신자)의 '대기중(PENDING)' 요청이 있으면 중복 생성 불가(409).
            - **전화번호 규칙(엄격)**: 하이픈/공백 없이 **숫자만**, **11자리**, **010 시작**.
            - 인증은 'Authorization' **쿠키(JWT)** 를 사용합니다.
            """,
            requestBody = @RequestBody(
                    required = true,
                    description = "상대방(시니어) 전화번호",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateLinkReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "올바른 예",
                                            value = "{ \"phone\": \"01012345678\" }"
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 예(하이픈 포함)",
                                            value = "{ \"phone\": \"010-1234-5678\" }"
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 예(11자 미만)",
                                            value = "{ \"phone\": \"0101234567\" }"
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 예(010 시작 아님)",
                                            value = "{ \"phone\": \"01112345678\" }"
                                    )
                            }
                    )
            ),
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "로그인 시 발급된 JWT가 들어있는 **쿠키**(Cookie). 헤더가 아닌 Cookie 로 전송됩니다.",
                            in = ParameterIn.COOKIE,
                            required = true,
                            examples = @ExampleObject(value = "Authorization=<jwt-token>")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "요청 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 12,
                              "status": "PENDING",
                              "recipientMaskedPhone": "010****5678"
                            }
                            """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 입력(전화번호 형식/본인에게 요청 등)", content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "전화번호 형식이 올바르지 않습니다. 전화번호는 하이픈/공백 없이 숫자만 입력해야 합니다."))),
            @ApiResponse(responseCode = "401", description = "인증 누락/만료(Authorization 쿠키 없음 또는 만료)", content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "인증 토큰이 없습니다."))),
            @ApiResponse(responseCode = "403", description = "보호자가 아닌 경우", content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "보호자만 연결 요청을 생성할 수 있습니다."))),
            @ApiResponse(responseCode = "404", description = "해당 전화번호의 사용자 없음", content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "해당 전화번호로 가입된 사용자가 없습니다."))),
            @ApiResponse(responseCode = "409", description = "이미 대기중(PENDING) 요청이 존재", content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "이미 대기 중인 요청이 있습니다.")))
    })
    @PostMapping
    public ResponseEntity<?> create(
            @org.springframework.web.bind.annotation.RequestBody CreateLinkReq in,
            HttpServletRequest req
    ) {
        try {
            Map<String, Object> result = linkRequestService.create(in, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null) {
                if (msg.contains("보호자만")) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
                if (msg.contains("전화") || msg.contains("형식") || msg.contains("본인"))
                    return ResponseEntity.badRequest().body(msg);
                if (msg.contains("인증") || msg.contains("만료"))
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
                if (msg.contains("없습니다"))
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(
            summary = "연결 요청 수락(시니어만)",
            description = """
            특정 연결 요청을 **수신자(시니어)** 가 수락합니다.
            - 경로의 {id}는 '연결 요청 ID' 입니다.
            - 수신자가 아닌 사용자 또는 시니어가 아닌 경우 403.
            - 인증은 Authorization **쿠키(JWT)** 를 사용합니다.
            """,
            parameters = {
                    @Parameter(name = "id", description = "연결 요청 ID", required = true, example = "12"),
                    @Parameter(
                            name = "Authorization",
                            description = "로그인 JWT가 담긴 쿠키",
                            in = ParameterIn.COOKIE,
                            required = true,
                            examples = @ExampleObject(value = "Authorization=<jwt-token>")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 수락 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"요청이 수락되었습니다.\" }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(이미 처리됨/요청 없음 등)", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "수신자가 아니거나 시니어가 아님", content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "시니어만 연결 요청을 수락할 수 있습니다.")))
    })
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(
            @PathVariable("id") Long id,
            HttpServletRequest req
    ) {
        try {
            linkRequestService.accept(id, req);
            return ResponseEntity.ok(Map.of("message", "요청이 수락되었습니다."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "서버 오류가 발생했습니다."));
        }
    }

    @Operation(
            summary = "연결 요청 거절(시니어만)",
            description = """
            특정 연결 요청을 **수신자(시니어)** 가 거절합니다.
            - 경로의 {id}는 '연결 요청 ID' 입니다.
            - 수신자가 아닌 사용자 또는 시니어가 아닌 경우 403.
            - Body는 없습니다.
            """,
            parameters = {
                    @Parameter(name = "id", description = "연결 요청 ID", required = true, example = "12"),
                    @Parameter(
                            name = "Authorization",
                            description = "로그인 JWT가 담긴 쿠키",
                            in = ParameterIn.COOKIE,
                            required = true,
                            examples = @ExampleObject(value = "Authorization=<jwt-token>")
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거절 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"message\": \"rejected\", \"id\": 12 }"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청/이미 처리됨", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "인증 만료/누락", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "수신자가 아니거나 시니어가 아님", content = @Content(mediaType = "text/plain",
                    examples = @ExampleObject(value = "시니어만 연결 요청을 거절할 수 있습니다."))),
            @ApiResponse(responseCode = "404", description = "요청을 찾을 수 없음", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable("id") Long id,
            HttpServletRequest req
    ) {
        try {
            linkRequestService.reject(id, req);
            return ResponseEntity.ok(Map.of("message", "rejected", "id", id));
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null) {
                if (msg.contains("인증")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
                if (msg.contains("권한") || msg.contains("시니어"))
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
                if (msg.contains("찾"))
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}