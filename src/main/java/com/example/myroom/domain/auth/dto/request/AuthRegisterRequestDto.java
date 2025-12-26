package com.example.myroom.domain.auth.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@JsonNaming(SnakeCaseStrategy.class)
public record AuthRegisterRequestDto(
        @Schema(description = "이름", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이름은 비어 있을 수 없습니다.")
        String name,

        @Schema(description = "이메일", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이메일은 비어 있을 수 없습니다.")
        String email,

        @Schema(description = "비밀번호", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "비밀번호는 비어 있을 수 없습니다.")
        String password
) {
    
}
