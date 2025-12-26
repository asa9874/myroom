package com.example.myroom.domain.model3D.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record ModelDimensionsUpdateRequestDto(
        @Schema(description = "변경할 가로 길이", requiredMode = RequiredMode.NOT_REQUIRED)
        Float width,
        
        @Schema(description = "변경할 세로 길이", requiredMode = RequiredMode.NOT_REQUIRED)
        Float length,
        
        @Schema(description = "변경할 높이 길이", requiredMode = RequiredMode.NOT_REQUIRED)
        Float height
) {
    
}
