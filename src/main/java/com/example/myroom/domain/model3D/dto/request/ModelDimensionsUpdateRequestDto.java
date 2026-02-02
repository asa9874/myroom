package com.example.myroom.domain.model3D.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "3D 모델 치수 정보 수정 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record ModelDimensionsUpdateRequestDto(
        @Schema(
            description = "변경할 가로 길이 (단위: cm, null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "55.0",
            minimum = "0"
        )
        Float width,
        
        @Schema(
            description = "변경할 세로 길이/깊이 (단위: cm, null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "48.5",
            minimum = "0"
        )
        Float length,
        
        @Schema(
            description = "변경할 높이 (단위: cm, null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "95.0",
            minimum = "0"
        )
        Float height
) {
    
}
