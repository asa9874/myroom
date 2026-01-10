package com.example.myroom.domain.model3D.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@JsonNaming(SnakeCaseStrategy.class)
public record Model3DCreateRequestDto(
        @Schema(description = "3D 모델 이름", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이름은 비어 있을 수 없습니다.")
        String name,
        
        @Schema(description = "3D 모델 링크", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "링크는 비어 있을 수 없습니다.")
        String link,
        
        @Schema(description = "공유 여부", requiredMode = RequiredMode.NOT_REQUIRED)
        @JsonProperty("is_shared")
        Boolean isShared,
        
        @Schema(description = "모델 설명", requiredMode = RequiredMode.NOT_REQUIRED)
        String description
) {
    
}
