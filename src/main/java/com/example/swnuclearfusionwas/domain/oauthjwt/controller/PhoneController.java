package com.example.swnuclearfusionwas.domain.oauthjwt.controller;

import com.example.swnuclearfusionwas.domain.oauthjwt.service.PhoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증/전화번호 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PhoneController {

    private final PhoneService phoneService;

    @Operation(
            summary = "전화번호 등록/변경",
            description = """
            쿠키 Authorization(JWT)에서 username을 읽어 해당 유저의 전화번호를 저장/변경합니다.
            - 전화번호는 숫자만 허용, 길이 11자리
            - 이미 다른 유저가 사용 중인 번호면 409 반환
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전화번호 설정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "message": "phone set",
                              "username": "kakao 4426578806",
                              "phone": "01012345678",
                              "status": 200
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "\"전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.\""))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Authorization 쿠키가 없습니다.\""))),
            @ApiResponse(responseCode = "409", description = "번호 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "\"이미 등록된 전화번호입니다.\"")))
    })
    @PostMapping("/phone")
    public ResponseEntity<?> setPhone(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "전화번호(숫자만, 11자리)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PhoneService.SetPhoneRequest.class),
                            examples = {
                                    @ExampleObject(name = "예시", value = "{ \"phone\": \"01012345678\" }")
                            })
            )
            @RequestBody PhoneService.SetPhoneRequest body,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Map<String, Object> result = phoneService.setPhone(body, request, response);
        int status = (int) result.getOrDefault("status", 200);
        return ResponseEntity.status(status).body(result);
    }
}