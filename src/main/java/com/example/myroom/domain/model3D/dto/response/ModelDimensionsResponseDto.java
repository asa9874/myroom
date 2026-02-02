package com.example.myroom.domain.model3D.dto.response;

import com.example.myroom.domain.model3D.model.ModelDimensions;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "3D 모델 치수 정보 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record ModelDimensionsResponseDto(
        @Schema(
            description = "치수 정보 고유 식별자", 
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        Long id,
        
        @Schema(
            description = "해당 치수 정보가 속한 3D 모델 ID", 
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        Long model3dId,
        
        @Schema(
            description = "3D 모델 가로 길이 (단위: cm)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "50.5"
        )
        Float width,
        
        @Schema(
            description = "3D 모델 세로 길이/깊이 (단위: cm)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "45.0"
        )
        Float length,
        
        @Schema(
            description = "3D 모델 높이 (단위: cm)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "90.0"
        )
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
