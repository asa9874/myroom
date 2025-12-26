package com.example.myroom.domain.recommand.dto.response;

import com.example.myroom.domain.recommand.model.DetectedFurniture;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record DetectedFurnitureResponseDto(
        @Schema(description = "탐지 가구 ID", requiredMode = RequiredMode.REQUIRED)
        Long id,
        
        @Schema(description = "방 이미지 ID", requiredMode = RequiredMode.REQUIRED)
        Long roomImageId,
        
        @Schema(description = "탐지된 객체명", requiredMode = RequiredMode.REQUIRED)
        String category,
        
        @Schema(description = "탐지 신뢰도", requiredMode = RequiredMode.REQUIRED)
        Float confidence,
        
        @Schema(description = "이미지 내 객체 위치 정보(JSON)", requiredMode = RequiredMode.REQUIRED)
        String positionMetadata
) {
    public static DetectedFurnitureResponseDto from(DetectedFurniture detectedFurniture) {
        return new DetectedFurnitureResponseDto(
                detectedFurniture.getId(),
                detectedFurniture.getRoomImage().getId(),
                detectedFurniture.getCategory(),
                detectedFurniture.getConfidence(),
                detectedFurniture.getPositionMetadata()
        );
    }
    
}
