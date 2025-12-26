package com.example.myroom.domain.member.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@JsonNaming(SnakeCaseStrategy.class)
public record MemberUpdateRequestDto( 
        @Schema(description = "변경할 이름", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이름은 비어 있을 수 없습니다.")
        String name,
        
        @Schema(description = "변경할 이메일", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이메일은 비어 있을 수 없습니다.")
        String email
) {
    
}