package com.example.myroom.domain.comment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 생성 요청 DTO")
public record CommentCreateRequestDto(
        @Schema(
            description = "게시글 ID",
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        @NotNull(message = "게시글 ID는 필수입니다.")
        @JsonProperty("post_id")
        Long postId,

        @Schema(
            description = "댓글 내용",
            requiredMode = RequiredMode.REQUIRED,
            example = "좋은 정보 감사합니다!"
        )
        @NotEmpty(message = "댓글 내용은 비어 있을 수 없습니다.")
        String content,

        @Schema(
            description = "부모 댓글 ID (대댓글 작성 시 입력, 없으면 null)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "1"
        )
        @JsonProperty("parent_comment_id")
        Long parentCommentId
) {
}