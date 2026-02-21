package com.example.myroom.domain.post.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.myroom.domain.post.like.dto.response.PostLikeResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "❤️ 게시글 좋아요", description = "게시글 좋아요/취소 API - 게시글당 1인 1좋아요만 허용됩니다.")
public interface PostLikeApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "좋아요 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostLikeResponseDto.class),
                    examples = @ExampleObject(
                        name = "좋아요 성공 응답",
                        value = """
                        {
                            "post_id": 1,
                            "like_count": 5,
                            "liked": true
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "이미 좋아요를 누른 게시글",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                        { "message": "이미 좋아요를 누른 게시글입니다." }
                    """)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "게시글을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "게시글 좋아요",
        description = "게시글에 좋아요를 등록합니다. 게시글당 1인 1좋아요만 허용됩니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/{postId}/likes")
    ResponseEntity<PostLikeResponseDto> like(
            @Parameter(description = "게시글 ID", required = true) @PathVariable(name = "postId") Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "좋아요 취소 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostLikeResponseDto.class),
                    examples = @ExampleObject(
                        name = "좋아요 취소 성공 응답",
                        value = """
                        {
                            "post_id": 1,
                            "like_count": 4,
                            "liked": false
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "좋아요를 누르지 않은 게시글",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                        { "message": "좋아요를 누르지 않은 게시글입니다." }
                    """)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "게시글을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "게시글 좋아요 취소",
        description = "게시글의 좋아요를 취소합니다. 좋아요를 누르지 않은 상태에서 취소 요청 시 예외가 발생합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/{postId}/likes")
    ResponseEntity<PostLikeResponseDto> unlike(
            @Parameter(description = "게시글 ID", required = true) @PathVariable(name = "postId") Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails member
    );
}
