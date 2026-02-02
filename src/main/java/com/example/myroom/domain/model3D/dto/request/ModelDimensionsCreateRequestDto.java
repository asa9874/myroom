package com.example.myroom.domain.model3D.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

@Schema(description = "3D 모델 치수 정보 생성 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record ModelDimensionsCreateRequestDto(
        @Schema(
            description = "치수 정보를 생성할 3D 모델 ID", 
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        @NotNull(message = "모델 ID는 비어 있을 수 없습니다.")
        Long model3dId,
        
        @Schema(
            description = "3D 모델 가로 길이 (단위: cm)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "50.5",
            minimum = "0"
        )
        Float width,
        
        @Schema(
            description = "3D 모델 세로 길이/깊이 (단위: cm)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "45.0",
            minimum = "0"
        )
        Float length,
        
        @Schema(
            description = "3D 모델 높이 (단위: cm)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "90.0",
            minimum = "0"
        )
        Float height
) {
    
}
