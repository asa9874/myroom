package com.example.myroom.domain.model3D.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "3D 모델 수정 요청 DTO v2 (link 필드 포함)")
@JsonNaming(SnakeCaseStrategy.class)
public record Model3DUpdateRequestV2Dto(
        @Schema(
            description = "변경할 3D 모델 이름 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "모던 소파",
            maxLength = 100
        )
        String name,
        
        @Schema(
            description = "변경할 공유 여부 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "true"
        )
        @JsonProperty("is_shared")
        Boolean isShared,
        
        @Schema(
            description = "변경할 모델 설명 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "편안한 3인용 소파입니다. 거실용으로 적합합니다.",
            maxLength = 1000
        )
        String description,
        
        @Schema(
            description = "변경할 3D 모델 링크 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "https://example.com/model3d/sofa.glb",
            maxLength = 2083
        )
        String link
) {
    
}