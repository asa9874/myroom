package com.example.myroom.domain.model3D.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.model3D.model.Model3D;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record Model3DResponseDto(
        @Schema(description = "모델 ID", requiredMode = RequiredMode.REQUIRED)
        Long id,
        
        @Schema(description = "모델 이름", requiredMode = RequiredMode.REQUIRED)
        String name,
        
        @Schema(description = "생성 일시", requiredMode = RequiredMode.REQUIRED)
        LocalDateTime createdAt,
        
        @Schema(description = "모델 링크", requiredMode = RequiredMode.REQUIRED)
        String link,
        
        @Schema(description = "생성자 ID", requiredMode = RequiredMode.REQUIRED)
        Long creatorId,
        
        @Schema(description = "공유 여부", requiredMode = RequiredMode.REQUIRED)
        @JsonProperty("is_shared")
        Boolean isShared,
        
        @Schema(description = "모델 설명", requiredMode = RequiredMode.REQUIRED)
        String description,

        @Schema(description = "썸네일 URL", requiredMode = RequiredMode.NOT_REQUIRED)
        String thumbnailUrl,

        @Schema(description = "VectorDB 학습 완료 여부", requiredMode = RequiredMode.REQUIRED)
        @JsonProperty("is_vector_db_trained")
        Boolean isVectorDbTrained
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
                model3D.getIsVectorDbTrained()
        );
    }
    
}
