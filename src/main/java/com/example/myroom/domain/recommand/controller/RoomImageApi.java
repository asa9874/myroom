package com.example.myroom.domain.recommand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.recommand.dto.request.RoomImageUploadRequestDto;
import com.example.myroom.domain.recommand.dto.response.RoomImageResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "방 이미지", description = "AI 추천을 위한 방 이미지 관리 및 조회")
public interface RoomImageApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "방 이미지 조회 성공"),
            @ApiResponse(responseCode = "404", description = "방 이미지를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "방 이미지 조회", description = "세션의 방 이미지를 조회")
    @GetMapping("")
    ResponseEntity<RoomImageResponseDto> getRoomImageById(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "방 이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "방 이미지 업로드", description = "새로운 방 이미지를 업로드")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("")
    ResponseEntity<RoomImageResponseDto> uploadRoomImage(
            @Parameter(description = "방 이미지 업로드 정보")
            @Valid @RequestBody RoomImageUploadRequestDto uploadRequestDto,
            @Parameter(description = "현재 인증된 사용자 정보 (JWT 토큰으로부터 자동 주입)", hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "방 이미지 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "방 이미지를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "방 이미지 삭제", description = "특정 방 이미지를 삭제")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("")
    ResponseEntity<Void> deleteRoomImage(
            @Parameter(description = "현재 인증된 사용자 정보 (JWT 토큰으로부터 자동 주입)", hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

}
