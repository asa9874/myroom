package com.example.myroom.admin.model3D.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.model3D.model.Model3D;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "관리자용 3D 모델 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record AdminModel3DResponseDto(
        @Schema(
            description = "3D 모델 고유 식별자", 
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        Long id,
        
        @Schema(
            description = "3D 모델 이름", 
            requiredMode = RequiredMode.REQUIRED,
            example = "모던 의자"
        )
        String name,
        
        @Schema(
            description = "3D 모델 생성 일시", 
            requiredMode = RequiredMode.REQUIRED,
            example = "2024-01-15T10:30:00"
        )
        LocalDateTime createdAt,
        
        @Schema(
            description = "3D 모델 파일 링크 (S3 URL 등)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "https://s3.amazonaws.com/myroom-bucket/models/chair.glb"
        )
        String link,
        
        @Schema(
            description = "3D 모델 생성자 회원 ID", 
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        Long creatorId,
        
        @Schema(
            description = "다른 사용자에게 공유 여부", 
            requiredMode = RequiredMode.REQUIRED,
            example = "true"
        )
        @JsonProperty("is_shared")
        Boolean isShared,
        
        @Schema(
            description = "3D 모델 상세 설명", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "모던 스타일의 회색 의자입니다."
        )
        String description
) {
    public static AdminModel3DResponseDto from(Model3D model3D) {
        return new AdminModel3DResponseDto(
                model3D.getId(),
                model3D.getName(),
                model3D.getCreatedAt(),
                model3D.getLink(),
                model3D.getCreatorId(),
                model3D.getIsShared(),
                model3D.getDescription()
        );
    }
    
}
