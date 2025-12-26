package com.example.myroom.domain.recommand.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.recommand.model.RecommendationSession;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record RecommendationSessionResponseDto(
        @Schema(description = "추천 세션 ID", requiredMode = RequiredMode.REQUIRED)
        Long id,
        
        @Schema(description = "방 이미지 ID", requiredMode = RequiredMode.REQUIRED)
        Long roomImageId,
        
        @Schema(description = "스타일 설명 문구", requiredMode = RequiredMode.REQUIRED)
        String styleCaption,
        
        @Schema(description = "인테리어 조언", requiredMode = RequiredMode.REQUIRED)
        String aiDesignerAdvice,
        
        @Schema(description = "추천 생성 일시", requiredMode = RequiredMode.REQUIRED)
        LocalDateTime recommendedAt
) {
    public static RecommendationSessionResponseDto from(RecommendationSession recommendationSession) {
        return new RecommendationSessionResponseDto(
                recommendationSession.getId(),
                recommendationSession.getRoomImage().getId(),
                recommendationSession.getStyleCaption(),
                recommendationSession.getAiDesignerAdvice(),
                recommendationSession.getRecommendedAt()
        );
    }
    
}
