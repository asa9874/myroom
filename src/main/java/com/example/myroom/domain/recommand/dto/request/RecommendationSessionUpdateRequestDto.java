package com.example.myroom.domain.recommand.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record RecommendationSessionUpdateRequestDto(
        @Schema(description = "변경할 스타일 설명 문구", requiredMode = RequiredMode.NOT_REQUIRED)
        String styleCaption,
        
        @Schema(description = "변경할 인테리어 조언", requiredMode = RequiredMode.NOT_REQUIRED)
        String aiDesignerAdvice
) {
    
}
