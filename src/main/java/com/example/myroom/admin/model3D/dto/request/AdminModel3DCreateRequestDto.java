package com.example.myroom.admin.model3D.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리자용 3D 모델 생성 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record AdminModel3DCreateRequestDto(
        @Schema(
            description = "3D 모델 이름", 
            requiredMode = RequiredMode.REQUIRED,
            example = "모던 의자",
            minLength = 1,
            maxLength = 100
        )
        @NotEmpty(message = "이름은 비어 있을 수 없습니다.")
        String name,
        
        @Schema(
            description = "3D 모델 파일 링크 (S3 URL 등)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "https://s3.amazonaws.com/myroom-bucket/models/chair.glb",
            minLength = 1,
            maxLength = 500
        )
        @NotEmpty(message = "링크는 비어 있을 수 없습니다.")
        String link,
        
        @Schema(
            description = "3D 모델 생성자 회원 ID (관리자가 직접 지정)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        @NotNull(message = "생성자 ID는 비어 있을 수 없습니다.")
        Long creatorId,
        
        @Schema(
            description = "다른 사용자에게 공유 여부 (true: 공개, false: 비공개)", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "true",
            defaultValue = "false"
        )
        @JsonProperty("is_shared")
        Boolean isShared,
        
        @Schema(
            description = "3D 모델에 대한 상세 설명", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "관리자가 생성한 모던 스타일의 의자입니다.",
            maxLength = 1000
        )
        String description
) {
    
}
