package com.example.myroom.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 갱신 응답 DTO")
public record AuthRefreshResponseDto(
        @Schema(
            description = "새로 발급된 JWT 액세스 토큰 (유효기간 1시간)",
            example = "eyJhbGciOiJIUzI1NiJ9.newaccess"
        )
        String token
) {
}
