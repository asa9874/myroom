package com.example.myroom.domain.recommand.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.recommand.dto.request.RecommendedModelCreateRequestDto;
import com.example.myroom.domain.recommand.dto.request.RecommendedModelUpdateRequestDto;
import com.example.myroom.domain.recommand.dto.response.RecommendedModelResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "추천된 3D 모델", description = "벡터 검색을 통해 사용자에게 매칭된 3D 가구 관리")
public interface RecommendedModelApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "세션의 추천된 모델 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 세션의 추천된 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "세션별 추천된 모델 조회", description = "특정 추천 세션의 모든 추천된 모델을 조회")
    @GetMapping("")
    ResponseEntity<List<RecommendedModelResponseDto>> getRecommendedModelsBySessionId(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "추천된 모델 조회 성공"),
            @ApiResponse(responseCode = "404", description = "추천된 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천된 모델 단건 조회", description = "추천된 모델 ID로 특정 모델 정보를 조회")
    @GetMapping("/{recommendedModelId}")
    ResponseEntity<RecommendedModelResponseDto> getRecommendedModelById(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "조회할 추천된 모델 ID")
            @PathVariable(name = "recommendedModelId") Long recommendedModelId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "추천된 모델 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "추천 세션을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천된 모델 생성", description = "새로운 추천된 모델을 생성")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("")
    ResponseEntity<RecommendedModelResponseDto> createRecommendedModel(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "추천된 모델 생성 정보")
            @Valid @RequestBody RecommendedModelCreateRequestDto createRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "추천된 모델 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "추천된 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천된 모델 수정", description = "특정 추천된 모델의 정보를 수정")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{recommendedModelId}")
    ResponseEntity<RecommendedModelResponseDto> updateRecommendedModel(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "수정할 추천된 모델 ID")
            @PathVariable(name = "recommendedModelId") Long recommendedModelId,
            @Parameter(description = "추천된 모델 수정 정보")
            @Valid @RequestBody RecommendedModelUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "추천된 모델 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "추천된 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천된 모델 삭제", description = "특정 추천된 모델을 삭제")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{recommendedModelId}")
    ResponseEntity<Void> deleteRecommendedModel(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "삭제할 추천된 모델 ID")
            @PathVariable(name = "recommendedModelId") Long recommendedModelId
    );

}
