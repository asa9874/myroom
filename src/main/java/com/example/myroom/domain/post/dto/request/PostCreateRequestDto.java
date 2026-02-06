package com.example.myroom.domain.post.dto.request;

import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.model.VisibilityScope;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "게시글 생성 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record PostCreateRequestDto(
        @Schema(
            description = "게시글 제목",
            requiredMode = RequiredMode.REQUIRED,
            example = "모던한 의자 추천해주세요"
        )
        @NotEmpty(message = "제목은 비어 있을 수 없습니다.")
        String title,

        @Schema(
            description = "게시글 내용",
            requiredMode = RequiredMode.REQUIRED,
            example = "거실에 어울릴 만한 모던한 의자를 찾고 있습니다."
        )
        @NotEmpty(message = "내용은 비어 있을 수 없습니다.")
        String content,

        @Schema(
            description = "카테고리 (FURNITURE, INTERIOR, QUESTION, REVIEW, ETC)",
            requiredMode = RequiredMode.REQUIRED,
            example = "QUESTION"
        )
        @NotNull(message = "카테고리는 필수입니다.")
        Category category,

        @Schema(
            description = "공개 범위 (PUBLIC, PRIVATE)",
            requiredMode = RequiredMode.REQUIRED,
            example = "PUBLIC",
            defaultValue = "PUBLIC"
        )
        VisibilityScope visibilityScope,

        @Schema(
            description = "3D 모델 ID",
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )        
        @JsonProperty("model3d_id")        
        Long model3dId
) {
}