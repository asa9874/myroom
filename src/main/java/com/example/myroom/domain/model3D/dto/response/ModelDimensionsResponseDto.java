package com.example.myroom.domain.model3D.dto.response;

import com.example.myroom.domain.model3D.model.ModelDimensions;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record ModelDimensionsResponseDto(
        @Schema(description = "치수 정보 ID", requiredMode = RequiredMode.REQUIRED)
        Long id,
        
        @Schema(description = "3D 모델 ID", requiredMode = RequiredMode.REQUIRED)
        Long model3dId,
        
        @Schema(description = "가로 길이", requiredMode = RequiredMode.REQUIRED)
        Float width,
        
        @Schema(description = "세로 길이", requiredMode = RequiredMode.REQUIRED)
        Float length,
        
        @Schema(description = "높이 길이", requiredMode = RequiredMode.REQUIRED)
        Float height
) {
    public static ModelDimensionsResponseDto from(ModelDimensions modelDimensions) {
        return new ModelDimensionsResponseDto(
                modelDimensions.getId(),
                modelDimensions.getModel3D().getId(),
                modelDimensions.getWidth(),
                modelDimensions.getLength(),
                modelDimensions.getHeight()
        );
    }
    
}
