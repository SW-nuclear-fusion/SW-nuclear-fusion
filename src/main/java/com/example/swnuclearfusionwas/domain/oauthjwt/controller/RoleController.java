package com.example.swnuclearfusionwas.domain.oauthjwt.controller;

import com.example.swnuclearfusionwas.domain.oauthjwt.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증/역할 관련 API")
@RestController
@RequestMapping("/api/auth")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(
            summary = "역할 선택(시니어/보호자)",
            description = """
            소셜 로그인 직후 호출합니다. 브라우저(또는 Postman)의 쿠키에 저장된 Authorization(JWT)에서 username을 읽어
            해당 유저의 역할을 DB에 저장하고, 새로운 권한(ROLE_*)이 포함된 JWT를 다시 쿠키에 설정합니다.
            - 요청 바디의 role 값은 SENIOR 또는 GUARADIAN(현재 enum 이름 그대로) 중 하나입니다.
            - 요청 시 Authorization 쿠키가 있어야 합니다(로그인 성공 후 발급됨).
            - 응답으로 Set-Cookie 헤더에 새 JWT가 내려옵니다.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "역할 설정 성공",
            headers = {
                    @Header(
                            name = "Set-Cookie",
                            description = "Authorization=<새JWT>",
                            schema = @Schema(type = "string")
                    )
            },
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class),
                    examples = @ExampleObject(
                            name = "success",
                            value = """
                {
                  "message": "role set",
                  "username": "kakao 4426578806",
                  "role": "SENIOR",
                  "token": "<new-jwt-token>"
                }
                """
                    )
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "인증 실패(쿠키 없음/만료 등)",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(example = "Authorization 쿠키가 없습니다.")
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청(유저 없음 등)",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(example = "유저를 찾을 수 없습니다: kakao 4426578806")
            )
    )
    @PostMapping("/role")
    public ResponseEntity<?> setRole(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "선택할 역할",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoleService.SetRoleRequest.class),
                            examples = {
                                    @ExampleObject(name = "SENIOR", value = "{ \"role\": \"SENIOR\" }"),
                                    @ExampleObject(name = "GUARADIAN", value = "{ \"role\": \"GUARADIAN\" }")
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody RoleService.SetRoleRequest requestBody,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Map<String, Object> result = roleService.assignRole(requestBody.role, request, response);
        int status = (int) result.get("status");
        result.remove("status");

        return ResponseEntity.status(status).body(result);
    }
}