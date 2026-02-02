package com.example.myroom.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "로그인 응답 DTO")
public record AuthLoginResponseDto(
        @Schema(
            description = "JWT 액세스 토큰 - Authorization 헤더에 Bearer {token} 형식으로 사용", 
            requiredMode = RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkhvbmcgR2lsZG9uZyIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        )
        String token
) {
    
}
