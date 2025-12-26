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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "🔐 인증", description = "회원가입 및 로그인")
public interface AuthApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "회원가입", description = "새로운 사용자 회원가입")
    @PostMapping("/register")
    ResponseEntity<Void> registerMember(
            @Parameter(description = "회원가입 요청 정보 (이름, 이메일, 비밀번호)")
            @Valid @RequestBody AuthRegisterRequestDto memberRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인")
    @PostMapping("/login")
    ResponseEntity<AuthLoginResponseDto> login(
            @Parameter(description = "로그인 요청 정보 (이메일, 비밀번호)")
            @Valid @RequestBody AuthLoginRequestDto requestDto
    );

}
