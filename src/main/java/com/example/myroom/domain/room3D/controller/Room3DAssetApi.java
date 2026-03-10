package com.example.myroom.domain.room3D.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.myroom.domain.room3D.dto.response.Room3DAssetResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "🏠 3D 룸 에셋", description = "3D 룸 에셋 조회 API - 사용 가능한 3D 에셋 목록을 제공합니다.")
public interface Room3DAssetApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "에셋 단건 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Room3DAssetResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "에셋을 찾을 수 없음",
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(value = "{\"message\": \"에셋을 찾을 수 없습니다. id=1\"}")
                )
            )
        }
    )
    @Operation(summary = "3D 에셋 단건 조회", description = "에셋 ID로 특정 3D 에셋 정보를 조회합니다.")
    @GetMapping("/{id}")
    ResponseEntity<Room3DAssetResponseDto> getAssetById(
            @Parameter(description = "조회할 에셋 ID", required = true, example = "1")
            @PathVariable(name = "id") Long id
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "에셋 전체 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Room3DAssetResponseDto.class))
                )
            )
        }
    )
    @Operation(summary = "3D 에셋 전체 조회", description = "등록된 모든 3D 에셋 목록을 조회합니다.")
    @GetMapping
    ResponseEntity<List<Room3DAssetResponseDto>> getAllAssets();
}
