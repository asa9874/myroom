package com.example.myroom.domain.post.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.model.Post;
import com.example.myroom.domain.post.model.VisibilityScope;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "게시글 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record PostResponseDto(
        @Schema(
            description = "게시글 고유 식별자",
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
            description = "3D 모델 ID",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "1"
        )
        Long model3dId,

        @Schema(
            description = "3D 모델 이름",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "모던 의자"
        )
        String model3dName,

        @Schema(
            description = "게시글 제목",
            requiredMode = RequiredMode.REQUIRED,
            example = "모던한 의자 추천해주세요"
        )
        String title,

        @Schema(
            description = "게시글 내용",
            requiredMode = RequiredMode.REQUIRED,
            example = "거실에 어울릴 만한 모던한 의자를 찾고 있습니다."
        )
        String content,

        @Schema(
            description = "카테고리",
            requiredMode = RequiredMode.REQUIRED,
            example = "QUESTION"
        )
        Category category,

        @Schema(
            description = "공개 범위",
            requiredMode = RequiredMode.REQUIRED,
            example = "PUBLIC"
        )
        VisibilityScope visibilityScope,

        @Schema(
            description = "조회수",
            requiredMode = RequiredMode.REQUIRED,
            example = "100"
        )
        Long viewCount,

        @Schema(
            description = "좋아요수",
            requiredMode = RequiredMode.REQUIRED,
            example = "15"
        )
        Long likeCount,

        @Schema(
            description = "게시글 작성 일시",
            requiredMode = RequiredMode.REQUIRED,
            example = "2024-01-15T10:30:00"
        )
        LocalDateTime createdAt,

        @Schema(
            description = "게시글 수정 일시",
            requiredMode = RequiredMode.REQUIRED,
            example = "2024-01-15T15:20:00"
        )
        LocalDateTime updatedAt
) {
    public static PostResponseDto from(Post post, long likeCount) {
        return new PostResponseDto(
                post.getId(),
                post.getMember().getId(),
                post.getMember().getName(),
                post.getModel3D() != null ? post.getModel3D().getId() : null,
                post.getModel3D() != null ? post.getModel3D().getName() : null,
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getVisibilityScope(),
                post.getViewCount() != null ? post.getViewCount() : 0L,
                likeCount,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}