package com.example.myroom.domain.recommand.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record RecommendedModelCreateRequestDto(
        @Schema(description = "추천 세션 ID", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "세션 ID는 비어 있을 수 없습니다.")
        Long sessionId,
        
        @Schema(description = "추천된 Model3D ID", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "모델 ID는 비어 있을 수 없습니다.")
        Long modelId,
        
        @Schema(description = "CLIP 코사인 유사도 점수", requiredMode = RequiredMode.NOT_REQUIRED)
        Float similarityScore,
        
        @Schema(description = "이 가구가 선택된 이유", requiredMode = RequiredMode.NOT_REQUIRED)
        String recommendationReason
) {
    
}
