package com.example.myroom.domain.room3D.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.room3D.dto.request.Room3DSessionCreateRequestDto;
import com.example.myroom.domain.room3D.dto.request.Room3DSessionUpdateInfoRequestDto;
import com.example.myroom.domain.room3D.dto.response.Room3DSessionResponseDto;
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
import jakarta.validation.Valid;

@Tag(name = "🎨 3D 룸 세션", description = "3D 룸 꾸미기 세션 관리 API - 사용자의 룸 꾸미기 작업을 저장하고 관리합니다.")
public interface Room3DSessionApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "세션 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Room3DSessionResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 - 유효성 검사 실패",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "에셋 또는 모델을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "새 세션 생성",
        description = "새로운 3D 룸 세션을 생성하고 XML 파일을 S3에 업로드합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Room3DSessionResponseDto> createSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "세션 생성 요청 정보")
            @RequestPart("data") @Valid Room3DSessionCreateRequestDto requestDto,
            @Parameter(description = "XML 파일")
            @RequestPart(value = "xmlFile", required = false) MultipartFile xmlFile
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "세션 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Room3DSessionResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "세션을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(summary = "세션 단건 조회", description = "세션 ID로 특정 세션 정보를 조회합니다.")
    @GetMapping("/{sessionId}")
    ResponseEntity<Room3DSessionResponseDto> getSessionById(
            @Parameter(description = "조회할 세션 ID", required = true, example = "1")
            @PathVariable(name = "sessionId") Long sessionId
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "내 세션 목록 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            )
        }
    )
    @Operation(
        summary = "내 세션 페이지네이션 조회",
        description = "현재 로그인한 사용자의 세션 목록을 페이지네이션으로 조회합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/my")
    ResponseEntity<Page<Room3DSessionResponseDto>> getMySessionsPaginated(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "XML 파일 업데이트 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Room3DSessionResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "권한 없음 또는 파일 누락",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "세션을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "세션 XML 파일 업데이트",
        description = "세션의 XML 파일을 새로운 파일로 교체합니다. 업데이트 날짜도 자동으로 갱신됩니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping(value = "/{sessionId}/xml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Room3DSessionResponseDto> updateSessionXml(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "업데이트할 세션 ID", required = true, example = "1")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "새로운 XML 파일", required = true)
            @RequestPart("xmlFile") MultipartFile xmlFile
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "세션 정보 업데이트 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Room3DSessionResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "권한 없음",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "세션을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "세션 정보 업데이트",
        description = "세션의 이름, 설명, 공유 여부, 사용된 3D 모델 목록을 업데이트합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PutMapping("/{sessionId}/info")
    ResponseEntity<Room3DSessionResponseDto> updateSessionInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "업데이트할 세션 ID", required = true, example = "1")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "세션 정보 업데이트 요청")
            @RequestBody @Valid Room3DSessionUpdateInfoRequestDto requestDto
    );
}
