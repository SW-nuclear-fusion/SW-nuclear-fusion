package com.example.swnuclearfusionwas.domain.oauthjwt.controller;

import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignInReqDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignInResDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.dto.SignUpReqDto;
import com.example.swnuclearfusionwas.domain.oauthjwt.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증/회원가입·로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(
            summary = "회원가입",
            description = "사용자 정보를 받아 회원가입을 처리합니다. 이미 존재하는 아이디일 경우 오류 메시지를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "\"회원가입 성공\""))
            ),
            @ApiResponse(responseCode = "409", description = "아이디 또는 전화번호 중복 또는 전화번호 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "아이디 중복", value = "\"이미 존재하는 아이디입니다.\""),
                                    @ExampleObject(name = "전화번호 중복", value = "\"이미 등록된 전화번호입니다.\""),
                                    @ExampleObject(name = "전화번호 형식 오류", value = "\"전화번호는 숫자만 입력 가능하며, 10자리여야 합니다.\"")
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpReqDto signUpReqDto) {
        try {
            userService.signup(signUpReqDto);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(
            summary = "로그인",
            description = "사용자 아이디와 비밀번호로 로그인하여 JWT 토큰을 반환합니다. 로그인 후, Authorization 헤더에 JWT 토큰을 포함하여 응답을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SignInResDto.class)),
                    headers = @Header(name = "Authorization", description = "<JWT 토큰>")),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 잘못된 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ \"accessToken\": null, \"name\": \"아이디 또는 비밀번호가 올바르지 않습니다.\", \"role\": null }"))
            )
    })
    @PostMapping("/signin")
    public ResponseEntity<SignInResDto> login(@RequestBody SignInReqDto signInReqDto, HttpServletResponse response) {
        try {
            SignInResDto signInResDto = userService.login(signInReqDto);
            response.setHeader("Authorization", signInResDto.getAccessToken());
            return ResponseEntity.ok(signInResDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SignInResDto(null, e.getMessage(), null));
        }
    }
}