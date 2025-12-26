package com.example.myroom.domain.recommand.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record RecommendedModelUpdateRequestDto(
        @Schema(description = "변경할 유사도 점수", requiredMode = RequiredMode.NOT_REQUIRED)
        Float similarityScore,
        
        @Schema(description = "변경할 추천 이유", requiredMode = RequiredMode.NOT_REQUIRED)
        String recommendationReason
) {
    
}
