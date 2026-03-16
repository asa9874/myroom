package com.example.myroom.domain.post.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.post.dto.response.PostResponseDto;
import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.model.VisibilityScope;
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

@Tag(name = "📝 게시글", description = "게시글 관리 및 조회 API - 게시글의 생성, 조회, 수정, 삭제 및 검색 기능을 제공합니다.")
public interface PostApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201", 
                description = "게시글 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostResponseDto.class),
                    examples = @ExampleObject(
                        name = "게시글 생성 성공 응답",
                        value = """
                        {
                            "id": 1,
                            "member_id": 1,
                            "member_name": "홍길동",
                            "model3d_id": null,
                            "model3d_name": null,
                            "image_url": "https://bucket.s3.amazonaws.com/images/photo1.png",
                            "image_urls": [
                                "https://bucket.s3.amazonaws.com/images/photo1.png",
                                "https://bucket.s3.amazonaws.com/images/photo2.png"
                            ],
                            "title": "모던한 의자 추천해주세요",
                            "content": "거실에 어울릴 만한 모던한 의자를 찾고 있습니다.",
                            "category": "QUESTION",
                            "visibility_scope": "PUBLIC",
                            "created_at": "2024-01-15T10:30:00",
                            "updated_at": "2024-01-15T10:30:00"
                        }
                        """
                    )
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
                responseCode = "404", 
                description = "3D 모델을 찾을 수 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "게시글 생성",
        description = "새로운 게시글을 생성합니다. images 파트로 최대 4장까지 업로드할 수 있으며 model3d_id 는 선택값입니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PostResponseDto> createPost(
            @Parameter(description = "게시글 이미지 파일 목록 (optional, 최대 4장)")
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @Parameter(description = "게시글 제목", required = true, example = "모던한 의자 추천")
            @RequestParam(value = "title") String title,
            @Parameter(description = "게시글 내용", required = true, example = "거실에 어울릴 의자")
            @RequestParam(value = "content") String content,
            @Parameter(description = "카테고리 (FURNITURE, INTERIOR, QUESTION, REVIEW, ETC)", required = true, example = "QUESTION")
            @RequestParam(value = "category") Category category,
            @Parameter(description = "공개 범위 (PUBLIC, PRIVATE)", example = "PUBLIC")
            @RequestParam(value = "visibility_scope", required = false) VisibilityScope visibilityScope,
                @Parameter(description = "3D 모델 ID (optional)", required = false, example = "1")
                @RequestParam(value = "model3d_id", required = false) Long model3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "게시글 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "비공개 게시글에 대한 접근 권한 없음", 
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
        summary = "게시글 상세 조회",
        description = "게시글 ID로 특정 게시글의 상세 정보를 조회합니다. 비공개 게시글은 작성자만 조회할 수 있습니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/{postId}")
    ResponseEntity<PostResponseDto> getPostById(
            @Parameter(description = "조회할 게시글 ID", required = true, example = "1")
            @PathVariable(name = "postId") Long postId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "게시글 수정 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "게시글 수정 권한 없음", 
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
        summary = "게시글 수정",
        description = "게시글 정보를 수정합니다. 작성자만 수정할 수 있습니다. retain_image_urls로 유지할 기존 이미지를 명시해야 하며, 미전달 시 기존 이미지는 모두 제거됩니다. images는 새 이미지 추가 업로드입니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PostResponseDto> updatePost(
            @Parameter(description = "수정할 게시글 ID", required = true, example = "1")
            @PathVariable(name = "postId") Long postId,
            @Parameter(description = "게시글 이미지 파일 목록 (optional, 최대 4장, 없으면 기존 유지)")
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @Parameter(description = "유지할 기존 이미지 URL 목록 (미전달 시 기존 이미지 전체 제거, 지정 시 해당 URL만 유지되고 images가 뒤에 추가됨)")
            @RequestParam(value = "retain_image_urls", required = false) List<String> retainImageUrls,
            @Parameter(description = "게시글 제목")
            @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "게시글 내용")
            @RequestParam(value = "content", required = false) String content,
            @Parameter(description = "카테고리")
            @RequestParam(value = "category", required = false) Category category,
            @Parameter(description = "공개 범위")
            @RequestParam(value = "visibility_scope", required = false) VisibilityScope visibilityScope,
            @Parameter(description = "3D 모델 ID")
            @RequestParam(value = "model3d_id", required = false) Long model3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204", 
                description = "게시글 삭제 성공"
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "게시글 삭제 권한 없음", 
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
        summary = "게시글 삭제",
        description = "게시글을 삭제합니다. 작성자만 삭제할 수 있습니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/{postId}")
    ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글 ID", required = true, example = "1")
            @PathVariable(name = "postId") Long postId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "공개 게시글 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            )
        }
    )
    @Operation(
        summary = "공개 게시글 목록 조회 (페이지네이션)",
        description = "공개된 게시글 목록을 페이지네이션으로 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/public")
    ResponseEntity<Page<PostResponseDto>> getPublicPosts(
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "카테고리별 게시글 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            )
        }
    )
    @Operation(
        summary = "카테고리별 게시글 목록 조회 (페이지네이션)",
        description = "특정 카테고리의 공개 게시글 목록을 페이지네이션으로 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/category/{category}")
    ResponseEntity<Page<PostResponseDto>> getPostsByCategory(
            @Parameter(description = "카테고리 (FURNITURE, INTERIOR, QUESTION, REVIEW, ETC)", 
                      required = true, example = "QUESTION")
            @PathVariable(name = "category") Category category,
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "내 게시글 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            )
        }
    )
    @Operation(
        summary = "내 게시글 목록 조회 (페이지네이션)",
        description = "내가 작성한 게시글 목록을 페이지네이션으로 조회합니다. (공개/비공개 모두 포함)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/my")
    ResponseEntity<Page<PostResponseDto>> getMyPosts(
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "게시글 검색 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            )
        }
    )
    @Operation(
        summary = "게시글 검색 (페이지네이션)",
        description = "제목으로 게시글을 검색합니다. 카테고리 필터링과 내 게시글 검색 옵션을 제공합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/search")  
    ResponseEntity<Page<PostResponseDto>> searchPosts(
            @Parameter(description = "검색할 제목 키워드", required = true, example = "의자")
            @RequestParam String title,
            @Parameter(description = "카테고리 필터 (선택사항)", example = "QUESTION")
            @RequestParam(required = false) Category category,
            @Parameter(description = "내 게시글만 검색할지 여부 (기본값: false)", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean myPost,
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );
}