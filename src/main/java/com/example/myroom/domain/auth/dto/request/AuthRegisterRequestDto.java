package com.example.myroom.domain.auth.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "회원가입 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record AuthRegisterRequestDto(
        @Schema(
            description = "사용자 이름 (실명 또는 닉네임)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "홍길동",
            minLength = 2,
            maxLength = 50
        )
        @NotEmpty(message = "이름은 비어 있을 수 없습니다.")
        String name,

        @Schema(
            description = "사용자 이메일 주소 (로그인 ID로 사용)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "user@example.com",
            minLength = 5,
            maxLength = 100
        )
        @NotEmpty(message = "이메일은 비어 있을 수 없습니다.")
        String email,

        @Schema(
            description = "사용자 비밀번호 (8자 이상 권장)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "password123!",
            minLength = 8,
            maxLength = 50
        )
        @NotEmpty(message = "비밀번호는 비어 있을 수 없습니다.")
        String password
) {
    
}
