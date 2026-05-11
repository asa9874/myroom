package com.example.myroom.domain.member.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "회원 활동 개수 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record MemberActivityCountResponseDto(
        @Schema(description = "작성한 게시글 수", example = "12", requiredMode = RequiredMode.REQUIRED)
        long postCount,

        @Schema(description = "작성한 댓글 수", example = "34", requiredMode = RequiredMode.REQUIRED)
        long commentCount,

        @Schema(description = "생성한 3D 모델 수", example = "5", requiredMode = RequiredMode.REQUIRED)
        long model3dCount
) {
    public static MemberActivityCountResponseDto of(long postCount, long commentCount, long model3dCount) {
        return new MemberActivityCountResponseDto(postCount, commentCount, model3dCount);
    }
}
