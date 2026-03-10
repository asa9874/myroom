package com.example.myroom.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "로그인 응답 DTO")
public record AuthLoginResponseDto(
        @Schema(
            description = "JWT 액세스 토큰 (유효기간 1시간) - Authorization 헤더에 Bearer {token} 형식으로 사용",
            requiredMode = RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzI1NiJ9.access"
        )
        String token,
        @Schema(
            description = "JWT 리프레시 토큰 (유효기간 30일) - 기기 내부 저장소에 안전하게 보관하여 /auth/refresh 호출 시 사용",
            requiredMode = RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzI1NiJ9.refresh"
        )
        String refreshToken
) {
}

