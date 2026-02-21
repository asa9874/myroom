package com.example.myroom.domain.comment.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.comment.model.Comment;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "댓글 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record CommentResponseDto(
        @Schema(
            description = "댓글 고유 식별자",
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        Long id,

        @Schema(
            description = "작성자 ID",
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        Long memberId,

        @Schema(
            description = "작성자 이름",
            requiredMode = RequiredMode.REQUIRED,
            example = "홍길동"
        )
        String memberName,

        @Schema(
            description = "게시글 ID",
            requiredMode = RequiredMode.REQUIRED,
            example = "1"
        )
        Long postId,

        @Schema(
            description = "댓글 내용",
            requiredMode = RequiredMode.REQUIRED,
            example = "좋은 정보 감사합니다!"
        )
        String content,

        @Schema(
            description = "작성일시",
            requiredMode = RequiredMode.REQUIRED,
            example = "2024-01-15T10:30:00"
        )
        LocalDateTime createdAt,

        @Schema(
            description = "수정일시",
            requiredMode = RequiredMode.REQUIRED,
            example = "2024-01-15T11:00:00"
        )
        LocalDateTime updatedAt,

        @Schema(
            description = "부모 댓글 ID (대댓글인 경우 해당 댓글 ID, 일반 댓글이면 null)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "1"
        )
        Long parentCommentId
) {
    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getName(),
                comment.getPost().getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null
        );
    }
}