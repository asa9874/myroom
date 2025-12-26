package com.example.myroom.domain.recommand.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonNaming(SnakeCaseStrategy.class)
public record DetectedFurnitureCreateRequestDto(
        @Schema(description = "방 이미지 ID", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "방 이미지 ID는 비어 있을 수 없습니다.")
        Long roomImageId,
        
        @Schema(description = "탐지된 객체명", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "카테고리는 비어 있을 수 없습니다.")
        String category,
        
        @Schema(description = "탐지 신뢰도", requiredMode = RequiredMode.NOT_REQUIRED)
        Float confidence,
        
        @Schema(description = "이미지 내 객체 위치 정보(JSON)", requiredMode = RequiredMode.NOT_REQUIRED)
        String positionMetadata
) {
    
}
