package com.example.myroom.domain.model3D.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@JsonNaming(SnakeCaseStrategy.class)
public record Model3DUploadRequestDto(
        @Schema(description = "가구 타입", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "가구 타입은 비어 있을 수 없습니다.")
        String furnitureType,
        
        @Schema(description = "3D 모델 이름", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이름은 비어 있을 수 없습니다.")
        String name,
        
        @Schema(description = "모델 설명", requiredMode = RequiredMode.NOT_REQUIRED)
        String description,
        
        @Schema(description = "공유 여부", requiredMode = RequiredMode.NOT_REQUIRED)
        @JsonProperty("is_shared")
        Boolean isShared
) {
}
