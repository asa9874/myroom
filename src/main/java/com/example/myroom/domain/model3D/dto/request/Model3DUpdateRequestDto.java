package com.example.myroom.domain.model3D.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record Model3DUpdateRequestDto(
        @Schema(description = "변경할 모델 이름", requiredMode = RequiredMode.NOT_REQUIRED)
        String name,
        
        @Schema(description = "변경할 공유 여부", requiredMode = RequiredMode.NOT_REQUIRED)
        Boolean isShared,
        
        @Schema(description = "변경할 모델 설명", requiredMode = RequiredMode.NOT_REQUIRED)
        String description
) {
    
}
