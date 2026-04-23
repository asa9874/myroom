package com.example.myroom.domain.room3D.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.example.myroom.domain.room3D.dto.response.Room3DResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "🏠 Room3D", description = "도면 이미지 기반 Room3D 생성/수정/조회/삭제 API")
public interface Room3DApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Room3D 생성 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room3DResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "인증되지 않음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "도면 이미지 업로드 및 Room3D 생성", security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Room3DResponseDto> createRoom3D(
            @Parameter(description = "도면 이미지 파일", required = true)
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @Parameter(description = "방 이름", required = true, example = "안방")
            @RequestParam(value = "room_name") String roomName,
            @Parameter(description = "방 설명", required = false, example = "붙박이장이 있는 안방")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Room3D 수정 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room3DResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Room3D를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "Room3D 수정", security = @SecurityRequirement(name = "Bearer Authentication"))
    @PutMapping(value = "/{room3dId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Room3DResponseDto> updateRoom3D(
            @Parameter(description = "Room3D ID", required = true, example = "1")
            @PathVariable(name = "room3dId") Long room3dId,
            @Parameter(description = "새 XML 파일", required = false)
            @RequestPart(value = "xml_file", required = false) MultipartFile xmlFile,
            @Parameter(description = "방 이름 (선택)", required = false, example = "거실")
            @RequestParam(value = "room_name", required = false) String roomName,
            @Parameter(description = "방 설명 (선택)", required = false, example = "창문이 큰 거실")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Room3D 단건 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Room3DResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Room3D를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "Room3D 단건 조회", security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{room3dId}")
    ResponseEntity<Room3DResponseDto> getRoom3DById(
            @Parameter(description = "Room3D ID", required = true, example = "1")
            @PathVariable(name = "room3dId") Long room3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "내 Room3D 목록 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
        }
    )
    @Operation(summary = "내 Room3D 목록 조회", security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/my")
    ResponseEntity<Page<Room3DResponseDto>> getMyRoom3Ds(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member,
            @PageableDefault(size = 10) Pageable pageable
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "Room3D 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "Room3D를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "Room3D 삭제", security = @SecurityRequirement(name = "Bearer Authentication"))
    @DeleteMapping("/{room3dId}")
    ResponseEntity<Void> deleteRoom3D(
            @Parameter(description = "Room3D ID", required = true, example = "1")
            @PathVariable(name = "room3dId") Long room3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );
}
