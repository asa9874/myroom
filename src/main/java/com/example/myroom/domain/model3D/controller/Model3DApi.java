package com.example.myroom.domain.model3D.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.model3D.dto.request.Model3DCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
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

@Tag(name = " 3D 모델", description = "사용자의 3D 모델 관리 및 조회")
public interface Model3DApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "3D 모델 조회 성공"),
            @ApiResponse(responseCode = "404", description = "3D 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 단건 조회", description = "3D 모델 ID로 특정 3D 모델의 정보를 조회")
    @GetMapping("/{model3dId}")
    ResponseEntity<Model3DResponseDto> getModel3DById(
            @Parameter(description = "조회할 3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "전체 3D 모델 조회 성공")
        }
    )
    @Operation(summary = "전체 3D 모델 조회", description = "모든 3D 모델의 목록을 조회 (공개된 모델과 본인의 모델만 조회 가능)")
    @GetMapping("/")
    ResponseEntity<List<Model3DResponseDto>> getAllModel3D();

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "3D 모델 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 생성", description = "새로운 3D 모델을 생성 (생성자는 자동으로 현재 로그인한 사용자로 설정)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/")
    ResponseEntity<Model3DResponseDto> createModel3D(
            @Parameter(description = "3D 모델 생성 정보")
            @Valid @RequestBody Model3DCreateRequestDto createRequestDto,
            @Parameter(description = "현재 인증된 사용자 정보 (JWT 토큰으로부터 자동 주입)", hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "3D 모델 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 모델은 수정할 수 없음)", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "3D 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 수정", description = "특정 3D 모델의 정보를 수정 (본인이 생성한 모델만 수정 가능)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{model3dId}")
    ResponseEntity<Model3DResponseDto> updateModel3D(
            @Parameter(description = "수정할 3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "3D 모델 수정 정보")
            @Valid @RequestBody Model3DUpdateRequestDto updateRequestDto,
            @Parameter(description = "현재 인증된 사용자 정보 (JWT 토큰으로부터 자동 주입)", hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "3D 모델 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (다른 사용자의 모델은 삭제할 수 없음)", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "3D 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 삭제", description = "특정 3D 모델을 삭제 (본인이 생성한 모델만 삭제 가능)")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{model3dId}")
    ResponseEntity<Void> deleteModel3D(
            @Parameter(description = "삭제할 3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "현재 인증된 사용자 정보 (JWT 토큰으로부터 자동 주입)", hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

}
