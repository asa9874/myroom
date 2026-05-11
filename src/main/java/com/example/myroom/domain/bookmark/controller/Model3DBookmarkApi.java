package com.example.myroom.domain.bookmark.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.myroom.domain.bookmark.dto.response.Model3DBookmarkResponseDto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
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

@Tag(name = "🔖 3D 모델 북마크", description = "3D 모델 북마크 API - 사용자별 중복 북마크는 허용되지 않습니다.")
public interface Model3DBookmarkApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "북마크 등록 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Model3DBookmarkResponseDto.class),
                    examples = @ExampleObject(
                        name = "북마크 등록 응답",
                        value = "{\"model3d_id\": 1, \"bookmarked\": true}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "이미 북마크한 3D 모델",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "3D 모델을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "3D 모델 북마크 등록",
        description = "3D 모델을 북마크합니다. 사용자당 동일 모델 중복 북마크는 허용되지 않습니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/{model3dId}/bookmarks")
    ResponseEntity<Model3DBookmarkResponseDto> bookmark(
            @Parameter(description = "3D 모델 ID", required = true) @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "북마크 해제 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Model3DBookmarkResponseDto.class),
                    examples = @ExampleObject(
                        name = "북마크 해제 응답",
                        value = "{\"model3d_id\": 1, \"bookmarked\": false}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "북마크하지 않은 3D 모델",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "3D 모델을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "3D 모델 북마크 해제",
        description = "3D 모델의 북마크를 해제합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/{model3dId}/bookmarks")
    ResponseEntity<Model3DBookmarkResponseDto> unbookmark(
            @Parameter(description = "3D 모델 ID", required = true) @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "북마크 여부 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "북마크 여부 응답",
                        value = "{\"bookmarked\": true}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "3D 모델을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "3D 모델 북마크 여부 확인",
        description = "현재 로그인한 사용자가 해당 3D 모델을 북마크했는지 확인합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/{model3dId}/bookmarks/me")
    ResponseEntity<Map<String, Boolean>> isBookmarked(
            @Parameter(description = "3D 모델 ID", required = true) @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "내 북마크한 3D 모델 목록 조회 성공",
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
        summary = "내 북마크한 3D 모델 목록 조회 (페이지네이션)",
        description = "로그인한 사용자가 북마크한 3D 모델 목록을 페이지네이션으로 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/bookmarks/my")
    ResponseEntity<Page<Model3DResponseDto>> getMyBookmarkedModel3Ds(
            @Parameter(description = "페이지네이션 정보") Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails member
    );
}
