package com.example.myroom.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.auth.dto.request.AuthLoginRequestDto;
import com.example.myroom.domain.auth.dto.request.AuthRegisterRequestDto;
import com.example.myroom.domain.auth.dto.response.AuthLoginResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "🔐 인증", description = "회원가입 및 로그인 API - 사용자 인증 관련 기능을 제공합니다.")
public interface AuthApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "회원가입 성공",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 필수 필드 누락 또는 유효성 검사 실패", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "유효성 검사 실패",
                        value = "{\"message\": \"이메일은 비어 있을 수 없습니다.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "이미 존재하는 이메일 - 중복된 이메일로 가입 시도", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "이메일 중복",
                        value = "{\"message\": \"이미 사용중인 이메일입니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "회원가입", 
        description = """
            새로운 사용자를 등록합니다.
            
            **요청 본문 예시:**
            ```json
            {
                "name": "홍길동",
                "email": "user@example.com",
                "password": "password123!"
            }
            ```
            
            **주의사항:**
            - 이메일은 중복될 수 없습니다.
            - 비밀번호는 8자 이상을 권장합니다.
            """
    )
    @PostMapping("/register")
    ResponseEntity<Void> registerMember(
            @Parameter(
                description = "회원가입 요청 정보",
                required = true
            )
            @Valid @RequestBody AuthRegisterRequestDto memberRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "관리자 계정 생성 성공",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 필수 필드 누락 또는 유효성 검사 실패", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "유효성 검사 실패",
                        value = "{\"error\": \"잘못된 인자 오류\", \"message\": \"이메일은 비어 있을 수 없습니다.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "이미 존재하는 이메일", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "이메일 중복",
                        value = "{\"error\": \"잘못된 인자 오류\", \"message\": \"이미 존재하는 이메일입니다: admin@example.com\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "관리자 계정 생성", 
        description = """
            새로운 관리자 계정을 생성합니다.
            
            **요청 본문 예시:**
            ```json
            {
                "name": "관리자",
                "email": "admin@example.com",
                "password": "admin123!@#"
            }
            ```
            
            **주의사항:**
            - ⚠️ 개발/테스트 환경에서만 사용하세요!
            - 이메일은 중복될 수 없습니다.
            - 생성된 계정은 ADMIN 역할로 설정됩니다.
            - 비밀번호는 8자 이상을 권장합니다.
            """
    )
    @PostMapping("/register-admin")
    ResponseEntity<Void> registerAdmin(
            @Parameter(
                description = "관리자 계정 생성 요청 정보",
                required = true
            )
            @Valid @RequestBody AuthRegisterRequestDto adminRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "로그인 성공 - JWT 토큰 반환",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthLoginResponseDto.class),
                    examples = @ExampleObject(
                        name = "로그인 성공 응답",
                        value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkhvbmcgR2lsZG9uZyIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 필수 필드 누락", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "필수 필드 누락",
                        value = "{\"message\": \"이메일은 비어 있을 수 없습니다.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증 실패 - 이메일 또는 비밀번호 불일치", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "인증 실패",
                        value = "{\"message\": \"이메일 또는 비밀번호가 올바르지 않습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "로그인", 
        description = """
            이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.
            
            **요청 본문 예시:**
            ```json
            {
                "email": "user@example.com",
                "password": "password123!"
            }
            ```
            
            **응답 예시:**
            ```json
            {
                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            }
            ```
            
            **토큰 사용법:**
            - 발급받은 토큰을 Authorization 헤더에 `Bearer {token}` 형식으로 포함하여 인증이 필요한 API를 호출합니다.
            """
    )
    @PostMapping("/login")
    ResponseEntity<AuthLoginResponseDto> login(
            @Parameter(
                description = "로그인 요청 정보",
                required = true
            )
            @Valid @RequestBody AuthLoginRequestDto requestDto
    );

}
