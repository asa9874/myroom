package com.example.myroom.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 갱신 요청 DTO")
public record AuthRefreshRequestDto(
        @NotBlank(message = "리프레시 토큰은 비어 있을 수 없습니다.")
        @Schema(description = "기기에 저장된 리프레시 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken
) {
}
