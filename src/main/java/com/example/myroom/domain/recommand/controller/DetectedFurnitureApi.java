package com.example.myroom.domain.recommand.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.recommand.dto.request.DetectedFurnitureCreateRequestDto;
import com.example.myroom.domain.recommand.dto.response.DetectedFurnitureResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "탐지된 가구", description = "YOLO 모델을 통해 탐지된 가구 정보 조회 및 생성")
public interface DetectedFurnitureApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "세션별 탐지된 가구 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 세션의 탐지된 가구를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "세션별 탐지된 가구 조회", description = "특정 추천 세션의 모든 탐지된 가구를 조회")
    @GetMapping("")
    ResponseEntity<List<DetectedFurnitureResponseDto>> getDetectedFurnitureByRoomImageId(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "탐지된 가구 조회 성공"),
            @ApiResponse(responseCode = "404", description = "탐지된 가구를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "탐지된 가구 단건 조회", description = "탐지된 가구 ID로 특정 가구 정보를 조회")
    @GetMapping("/{detectedFurnitureId}")
    ResponseEntity<DetectedFurnitureResponseDto> getDetectedFurnitureById(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "조회할 탐지된 가구 ID")
            @PathVariable(name = "detectedFurnitureId") Long detectedFurnitureId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "탐지된 가구 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "추천 세션을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "탐지된 가구 생성", description = "새로운 탐지된 가구 정보를 생성")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("")
    ResponseEntity<DetectedFurnitureResponseDto> createDetectedFurniture(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "탐지된 가구 생성 정보")
            @Valid @RequestBody DetectedFurnitureCreateRequestDto createRequestDto
    );

}
