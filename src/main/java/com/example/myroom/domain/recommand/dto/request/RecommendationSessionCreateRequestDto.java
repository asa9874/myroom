package com.example.myroom.domain.recommand.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record RecommendationSessionCreateRequestDto(
        @Schema(description = "방 이미지 ID", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "방 이미지 ID는 비어 있을 수 없습니다.")
        Long roomImageId,
        
        @Schema(description = "BLIP이 추출한 스타일 설명 문구", requiredMode = RequiredMode.NOT_REQUIRED)
        String styleCaption,
        
        @Schema(description = "Gemini가 제안한 인테리어 조언", requiredMode = RequiredMode.NOT_REQUIRED)
        String aiDesignerAdvice
) {
    
}
