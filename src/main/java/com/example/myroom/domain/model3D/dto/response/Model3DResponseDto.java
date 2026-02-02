package com.example.myroom.domain.model3D.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.model3D.model.Model3D;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "3D 모델 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record Model3DResponseDto(
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
            example = "false"
        )
        @JsonProperty("is_shared")
        Boolean isShared,
        
        @Schema(
            description = "3D 모델 상세 설명", 
            requiredMode = RequiredMode.REQUIRED,
            example = "모던 스타일의 회색 의자입니다."
        )
        String description,

        @Schema(
            description = "3D 모델 썸네일 이미지 URL", 
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "https://s3.amazonaws.com/myroom-bucket/thumbnails/chair_thumb.png"
        )
        String thumbnailUrl,

        @Schema(
            description = "VectorDB 학습 완료 여부 - AI 추천 시스템에서 사용 가능 여부", 
            requiredMode = RequiredMode.REQUIRED,
            example = "true"
        )
        @JsonProperty("is_vector_db_trained")
        Boolean isVectorDbTrained,

        @Schema(
            description = "3D 모델 생성 상태 (PROCESSING: 처리중, SUCCESS: 완료, FAILED: 실패)", 
            requiredMode = RequiredMode.REQUIRED,
            example = "SUCCESS",
            allowableValues = {"PROCESSING", "SUCCESS", "FAILED"}
        )
        String status
) {
    public static Model3DResponseDto from(Model3D model3D) {
        return new Model3DResponseDto(
                model3D.getId(),
                model3D.getName(),
                model3D.getCreatedAt(),
                model3D.getLink(),
                model3D.getCreatorId(),
                model3D.getIsShared(),
                model3D.getDescription(),
                model3D.getThumbnailUrl(),
                model3D.getIsVectorDbTrained(),
                model3D.getStatus()
        );
    }
    
}
