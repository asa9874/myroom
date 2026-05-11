package com.example.myroom.domain.bookmark.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "3D 모델 북마크 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record Model3DBookmarkResponseDto(
    @Schema(description = "3D 모델 ID", example = "1")
        Long model3dId,

    @Schema(description = "현재 북마크 여부", example = "true")
        boolean bookmarked
) {
    public static Model3DBookmarkResponseDto of(Long model3dId, boolean bookmarked) {
        return new Model3DBookmarkResponseDto(model3dId, bookmarked);
    }
}
