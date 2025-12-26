package com.example.myroom.domain.recommand.dto.response;

import com.example.myroom.domain.recommand.model.RecommendedModel;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record RecommendedModelResponseDto(
        @Schema(description = "추천된 모델 ID", requiredMode = RequiredMode.REQUIRED)
        Long id,
        
        @Schema(description = "추천 세션 ID", requiredMode = RequiredMode.REQUIRED)
        Long sessionId,
        
        @Schema(description = "3D 모델 ID", requiredMode = RequiredMode.REQUIRED)
        Long modelId,
        
        @Schema(description = "유사도 점수", requiredMode = RequiredMode.REQUIRED)
        Float similarityScore,
        
        @Schema(description = "추천 이유", requiredMode = RequiredMode.REQUIRED)
        String recommendationReason
) {
    public static RecommendedModelResponseDto from(RecommendedModel recommendedModel) {
        return new RecommendedModelResponseDto(
                recommendedModel.getId(),
                recommendedModel.getRecommendationSession().getId(),
                recommendedModel.getModelId(),
                recommendedModel.getSimilarityScore(),
                recommendedModel.getRecommendationReason()
        );
    }
    
}
