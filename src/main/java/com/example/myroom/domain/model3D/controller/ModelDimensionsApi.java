package com.example.myroom.domain.model3D.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.model3D.dto.request.ModelDimensionsCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.ModelDimensionsUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.ModelDimensionsResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "3D 모델 치수", description = "3D 모델의 상세 치수 정보 관리 및 조회")
public interface ModelDimensionsApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "3D 모델의 치수 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "3D 모델의 치수 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 치수 정보 조회", description = "3D 모델 ID로 해당 모델의 치수 정보를 조회")
    @GetMapping("")
    ResponseEntity<ModelDimensionsResponseDto> getModelDimensionsByModel3DId(
            @Parameter(description = "조회할 3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "치수 정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "3D 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "치수 정보 생성", description = "3D 모델에 대한 새로운 치수 정보를 생성")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("")
    ResponseEntity<ModelDimensionsResponseDto> createModelDimensions(
            @Parameter(description = "3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "치수 정보 생성 정보")
            @Valid @RequestBody ModelDimensionsCreateRequestDto createRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "치수 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "치수 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "치수 정보 수정", description = "3D 모델의 치수 정보를 수정")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("")
    ResponseEntity<ModelDimensionsResponseDto> updateModelDimensions(
            @Parameter(description = "3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "치수 정보 수정 정보")
            @Valid @RequestBody ModelDimensionsUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "치수 정보 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "치수 정보를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "치수 정보 삭제", description = "3D 모델의 치수 정보를 삭제")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("")
    ResponseEntity<Void> deleteModelDimensions(
            @Parameter(description = "3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId
    );

}
