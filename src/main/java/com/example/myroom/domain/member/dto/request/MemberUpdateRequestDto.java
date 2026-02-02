package com.example.myroom.domain.member.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "회원 정보 수정 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record MemberUpdateRequestDto( 
        @Schema(
            description = "변경할 사용자 이름", 
            requiredMode = RequiredMode.REQUIRED,
            example = "김철수",
            minLength = 2,
            maxLength = 50
        )
        @NotEmpty(message = "이름은 비어 있을 수 없습니다.")
        String name,
        
        @Schema(
            description = "변경할 이메일 주소", 
            requiredMode = RequiredMode.REQUIRED,
            example = "newuser@example.com",
            minLength = 5,
            maxLength = 100
        )
        @NotEmpty(message = "이메일은 비어 있을 수 없습니다.")
        String email
) {
    
}