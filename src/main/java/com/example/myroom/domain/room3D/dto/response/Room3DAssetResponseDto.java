package com.example.myroom.domain.room3D.dto.response;

import com.example.myroom.domain.room3D.model.Room3DAsset;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "3D 룸 에셋 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record Room3DAssetResponseDto(

    @Schema(description = "에셋 고유 식별자", requiredMode = RequiredMode.REQUIRED, example = "1")
    Long id,

    @Schema(description = "에셋 이름", requiredMode = RequiredMode.REQUIRED, example = "거실룸")
    String name,

    @Schema(description = "에셋 설명", example = "아늑한 거실 스타일의 3D 룸 에셋입니다.")
    String description,

    @Schema(description = "썸네일 이미지 링크", example = "https://cdn.example.com/thumbnails/living-room.png")
    String thumbnailUrl,

    @Schema(description = "3D 모델 파일 링크", requiredMode = RequiredMode.REQUIRED, example = "https://cdn.example.com/models/living-room.glb")
    String modelUrl
) {
    public static Room3DAssetResponseDto from(Room3DAsset asset) {
        return new Room3DAssetResponseDto(
                asset.getId(),
                asset.getName(),
                asset.getDescription(),
                asset.getThumbnailUrl(),
                asset.getModelUrl()
        );
    }
}
