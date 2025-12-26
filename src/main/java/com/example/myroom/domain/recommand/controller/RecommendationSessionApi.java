package com.example.myroom.domain.recommand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.recommand.dto.request.RecommendationSessionCreateRequestDto;
import com.example.myroom.domain.recommand.dto.request.RecommendationSessionUpdateRequestDto;
import com.example.myroom.domain.recommand.dto.response.RecommendationSessionResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "추천 세션", description = "BLIP 및 Gemini를 통해 생성된 스타일 분석 및 제안 관리")
public interface RecommendationSessionApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "추천 세션 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "방 이미지를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천 세션 생성", description = "새로운 추천 세션을 생성")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("")
    ResponseEntity<RecommendationSessionResponseDto> createRecommendationSession(
            @Parameter(description = "추천 세션 생성 정보")
            @Valid @RequestBody RecommendationSessionCreateRequestDto createRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "추천 세션 조회 성공"),
            @ApiResponse(responseCode = "404", description = "추천 세션을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천 세션 단건 조회", description = "추천 세션 ID로 특정 추천 세션 정보를 조회")
    @GetMapping("/{sessionId}")
    ResponseEntity<RecommendationSessionResponseDto> getRecommendationSessionById(
            @Parameter(description = "조회할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "추천 세션 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "추천 세션을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천 세션 수정", description = "특정 추천 세션의 정보를 수정")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{sessionId}")
    ResponseEntity<RecommendationSessionResponseDto> updateRecommendationSession(
            @Parameter(description = "수정할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId,
            @Parameter(description = "추천 세션 수정 정보")
            @Valid @RequestBody RecommendationSessionUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "추천 세션 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "추천 세션을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "추천 세션 삭제", description = "특정 추천 세션을 삭제")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{sessionId}")
    ResponseEntity<Void> deleteRecommendationSession(
            @Parameter(description = "삭제할 추천 세션 ID")
            @PathVariable(name = "sessionId") Long sessionId
    );

}
