package com.example.myroom.domain.comment.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.myroom.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.myroom.domain.comment.dto.response.CommentResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "💬 댓글", description = "댓글 관리 및 조회 API - 댓글의 생성, 조회, 수정, 삭제 기능을 제공합니다.")
public interface CommentApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201", 
                description = "댓글 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommentResponseDto.class),
                    examples = @ExampleObject(
                        name = "댓글 생성 성공 응답",
                        value = """
                        {
                            "id": 1,
                            "member_id": 1,
                            "member_name": "홍길동",
                            "post_id": 1,
                            "content": "좋은 정보 감사합니다!",
                            "created_at": "2024-01-15T10:30:00",
                            "updated_at": "2024-01-15T10:30:00",
                            "parent_comment_id": null
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 (유효하지 않은 입력값 또는 대댓글 규칙 위반)", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않은 사용자", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "게시글 또는 부모 댓글을 찾을 수 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "댓글 생성",
        description = "게시글에 새로운 댓글을 생성합니다. parent_comment_id를 전달하면 대댓글로 작성됩니다. 대댓글에도 대댓글 작성이 가능합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping
    ResponseEntity<CommentResponseDto> createComment(
            @Parameter(description = "댓글 생성 요청 데이터", required = true)
            @Valid @RequestBody CommentCreateRequestDto requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "댓글 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommentResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "댓글을 찾을 수 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "댓글 상세 조회",
        description = "댓글 ID로 특정 댓글을 조회합니다."
    )
    @GetMapping("/{commentId}")
    ResponseEntity<CommentResponseDto> getCommentById(
            @Parameter(description = "조회할 댓글의 고유 ID", required = true, example = "1")
            @PathVariable(name = "commentId") Long commentId
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "게시글의 댓글 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommentResponseDto.class))
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "게시글을 찾을 수 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "게시글의 댓글 목록 조회",
        description = "특정 게시글의 모든 댓글을 작성일순으로 조회합니다."
    )
    @GetMapping("/post/{postId}")
    ResponseEntity<List<CommentResponseDto>> getCommentsByPostId(
            @Parameter(description = "댓글을 조회할 게시글의 고유 ID", required = true, example = "1")
            @PathVariable(name = "postId") Long postId
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "게시글의 댓글 목록 조회 성공 (페이지네이션)",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "게시글을 찾을 수 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "게시글의 댓글 목록 조회 (페이지네이션)",
        description = "특정 게시글의 댓글을 페이지네이션으로 조회합니다."
    )
    @GetMapping("/post/{postId}/page")
    ResponseEntity<Page<CommentResponseDto>> getCommentsByPostIdWithPagination(
            @Parameter(description = "댓글을 조회할 게시글의 고유 ID", required = true, example = "1")
            @PathVariable(name = "postId") Long postId,
            @Parameter(hidden = true)
            Pageable pageable
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "내 댓글 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않은 사용자", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "내 댓글 목록 조회",
        description = "로그인한 사용자가 작성한 댓글 목록을 페이지네이션으로 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/my")
    ResponseEntity<Page<CommentResponseDto>> getMyComments(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member,
            @Parameter(hidden = true)
            Pageable pageable
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "댓글 수정 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommentResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 (유효하지 않은 입력값)", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않은 사용자", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "수정 권한 없음 (본인 댓글이 아님)", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "댓글을 찾을 수 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "댓글 수정",
        description = "본인이 작성한 댓글의 내용을 수정합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping("/{commentId}")
    ResponseEntity<CommentResponseDto> updateComment(
            @Parameter(description = "수정할 댓글의 고유 ID", required = true, example = "1")
            @PathVariable(name = "commentId") Long commentId,
            @Parameter(description = "댓글 수정 요청 데이터", required = true)
            @Valid @RequestBody CommentUpdateRequestDto requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204", 
                description = "댓글 삭제 성공",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않은 사용자", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "삭제 권한 없음 (본인 댓글이 아님)", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "댓글을 찾을 수 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "댓글 삭제",
        description = "본인이 작성한 댓글을 삭제합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/{commentId}")
    ResponseEntity<Void> deleteComment(
            @Parameter(description = "삭제할 댓글의 고유 ID", required = true, example = "1")
            @PathVariable(name = "commentId") Long commentId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );
}