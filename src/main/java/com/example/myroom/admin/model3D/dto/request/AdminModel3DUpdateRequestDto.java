package com.example.myroom.admin.model3D.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "관리자용 3D 모델 수정 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record AdminModel3DUpdateRequestDto(
        @Schema(
            description = "변경할 3D 모델 이름 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "모던 소파",
            maxLength = 100
        )
        String name,
        
        @Schema(
            description = "변경할 3D 모델 파일 링크 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "https://s3.amazonaws.com/myroom-bucket/models/sofa.glb",
            maxLength = 500
        )
        String link,
        
        @Schema(
            description = "변경할 생성자 회원 ID (관리자 전용, null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "2"
        )
        Long creatorId,
        
        @Schema(
            description = "변경할 공유 여부 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "false"
        )
        @JsonProperty("is_shared")
        Boolean isShared,
        
        @Schema(
            description = "변경할 모델 설명 (null이면 변경 없음)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "수정된 모델 설명입니다.",
            maxLength = 1000
        )
        String description
) {
    
}
