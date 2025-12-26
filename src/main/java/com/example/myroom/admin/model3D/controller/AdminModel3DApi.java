package com.example.myroom.admin.model3D.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.admin.model3D.dto.request.AdminModel3DCreateRequestDto;
import com.example.myroom.admin.model3D.dto.request.AdminModel3DUpdateRequestDto;
import com.example.myroom.admin.model3D.dto.response.AdminModel3DResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "⚙️ 관리자 - 3D 모델", description = "관리자 전용 3D 모델 관리")
public interface AdminModel3DApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "3D 모델 조회 성공"),
            @ApiResponse(responseCode = "404", description = "3D 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 단건 조회", description = "관리자가 특정 3D 모델의 정보를 조회")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{model3dId}")
    ResponseEntity<AdminModel3DResponseDto> getModel3DById(
            @Parameter(description = "조회할 3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "전체 3D 모델 조회 성공")
        }
    )
    @Operation(summary = "전체 3D 모델 조회", description = "관리자가 모든 3D 모델의 목록을 조회")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/")
    ResponseEntity<List<AdminModel3DResponseDto>> getAllModel3D();

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "3D 모델 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 생성", description = "관리자가 새로운 3D 모델을 생성 (ADMIN만 가능)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/")
    
    ResponseEntity<AdminModel3DResponseDto> createModel3D(
            @Parameter(description = "생성할 3D 모델 정보 (링크, 작성자ID, 공유여부, 설명)")
            @Valid @RequestBody AdminModel3DCreateRequestDto createRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "3D 모델 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "3D 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 수정", description = "관리자가 특정 3D 모델을 수정 (ADMIN만 가능, creatorId 변경 가능)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{model3dId}")
    ResponseEntity<AdminModel3DResponseDto> updateModel3D(
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "수정할 3D 모델 정보 (링크, 작성자ID, 공유여부, 설명)")
            @Valid @RequestBody AdminModel3DUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "3D 모델 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "3D 모델을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "3D 모델 삭제", description = "관리자가 특정 3D 모델을 삭제 (ADMIN만 가능)")
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<Void> deleteModel3D(
            @Parameter(description = "삭제할 3D 모델 ID")
            @PathVariable(name = "model3dId") Long model3dId
    );

}
