package com.example.myroom.domain.post.dto.request;

import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.model.VisibilityScope;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "게시글 수정 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record PostUpdateRequestDto(
        @Schema(
            description = "게시글 제목 (수정할 경우)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "수정된 게시글 제목"
        )
        String title,

        @Schema(
            description = "게시글 내용 (수정할 경우)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "수정된 게시글 내용입니다."
        )
        String content,

        @Schema(
            description = "카테고리 (수정할 경우)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "REVIEW"
        )
        Category category,

        @Schema(
            description = "공개 범위 (수정할 경우)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "PRIVATE"
        )
        VisibilityScope visibilityScope,

        @Schema(
            description = "3D 모델 ID (수정할 경우)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "2"
        )
        @JsonProperty("model3d_id")
        Long model3dId
) {
}