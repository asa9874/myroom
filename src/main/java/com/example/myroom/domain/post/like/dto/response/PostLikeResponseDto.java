package com.example.myroom.domain.post.like.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record PostLikeResponseDto(
        @Schema(description = "게시글 ID", example = "1")
        Long postId,

        @Schema(description = "현재 좋아요 수", example = "5")
        long likeCount,

        @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
        boolean liked
) {
    public static PostLikeResponseDto of(Long postId, long likeCount, boolean liked) {
        return new PostLikeResponseDto(postId, likeCount, liked);
    }
}
