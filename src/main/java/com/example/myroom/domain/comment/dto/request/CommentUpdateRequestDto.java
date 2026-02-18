package com.example.myroom.domain.comment.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "댓글 수정 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record CommentUpdateRequestDto(
        @Schema(
            description = "댓글 내용",
            requiredMode = RequiredMode.REQUIRED,
            example = "수정된 댓글 내용입니다."
        )
        @NotEmpty(message = "댓글 내용은 비어 있을 수 없습니다.")
        String content
) {
}