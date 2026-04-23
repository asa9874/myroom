package com.example.myroom.domain.model3D.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.model3D.dto.request.ModelDimensionsCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.ModelDimensionsUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.ModelDimensionsResponseDto;
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

@Tag(name = "📐 3D 모델 치수", description = "3D 모델의 상세 치수 정보 관리 및 조회 API - 모델의 가로/세로/높이 치수 정보를 관리합니다.")
public interface ModelDimensionsApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "치수 정보 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModelDimensionsResponseDto.class),
                    examples = @ExampleObject(
                        name = "치수 정보 조회 응답",
                        value = """
                            {
                                "id": 1,
                                "model3d_id": 1,
                                "width": 50.5,
                                "length": 45.0,
                                "height": 90.0
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "3D 모델 또는 치수 정보를 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "치수 정보 없음",
                        value = "{\"message\": \"해당 3D 모델의 치수 정보를 찾을 수 없습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "3D 모델 치수 정보 조회", 
        description = """
            3D 모델 ID로 해당 모델의 치수 정보를 조회합니다.
            
            **응답 예시:**
            ```json
            {
                "id": 1,
                "model3d_id": 1,
                "width": 50.5,
                "length": 45.0,
                "height": 90.0
            }
            ```
            
            **단위:** 모든 치수는 센티미터(cm) 단위입니다.
            """
    )
    @GetMapping("")
    ResponseEntity<ModelDimensionsResponseDto> getModelDimensionsByModel3DId(
            @Parameter(
                description = "조회할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201", 
                description = "치수 정보 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModelDimensionsResponseDto.class),
                    examples = @ExampleObject(
                        name = "생성 성공 응답",
                        value = """
                            {
                                "id": 1,
                                "model3d_id": 1,
                                "width": 50.5,
                                "length": 45.0,
                                "height": 90.0
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 유효성 검사 실패", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "유효성 검사 실패",
                        value = "{\"message\": \"모델 ID는 비어 있을 수 없습니다.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "3D 모델을 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "모델 없음",
                        value = "{\"message\": \"해당 3D 모델을 찾을 수 없습니다.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "이미 치수 정보가 존재함", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "중복",
                        value = "{\"message\": \"해당 3D 모델의 치수 정보가 이미 존재합니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "치수 정보 생성", 
        description = """
            3D 모델에 대한 새로운 치수 정보를 생성합니다.
            
            **인증 필요:** Bearer Token
            
            **요청 본문 예시:**
            ```json
            {
                "model3d_id": 1,
                "width": 50.5,
                "length": 45.0,
                "height": 90.0
            }
            ```
            
            **단위:** 모든 치수는 센티미터(cm) 단위로 입력합니다.
            
            **주의사항:**
            - 하나의 3D 모델에는 하나의 치수 정보만 생성 가능합니다.
            - 이미 치수 정보가 있는 경우 수정 API를 사용하세요.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("")
    ResponseEntity<ModelDimensionsResponseDto> createModelDimensions(
            @Parameter(
                description = "치수 정보를 생성할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(
                description = "생성할 치수 정보",
                required = true
            )
            @Valid @RequestBody ModelDimensionsCreateRequestDto createRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "치수 이미지 분석 요청 성공",
                content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                        name = "요청 성공",
                        value = "치수 분석 요청이 완료되었습니다. 결과는 WebSocket으로 전달됩니다."
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 - 파일 누락 또는 비이미지 파일",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "3D 모델을 찾을 수 없음",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "치수 이미지 분석 요청",
        description = "가구 사이즈가 포함된 이미지를 업로드하면 S3 업로드 후 MQ를 통해 AI 서버에 치수 추출을 요청합니다. 결과는 WebSocket으로 전달됩니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping(value = "/request-by-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> requestModelDimensionsByImage(
            @Parameter(
                description = "치수 추출을 요청할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "가구 사이즈가 포함된 이미지 파일", required = true)
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "치수 정보 수정 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModelDimensionsResponseDto.class),
                    examples = @ExampleObject(
                        name = "수정 성공 응답",
                        value = """
                            {
                                "id": 1,
                                "model3d_id": 1,
                                "width": 55.0,
                                "length": 48.5,
                                "height": 95.0
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 유효성 검사 실패", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "치수 정보를 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "치수 정보 없음",
                        value = "{\"message\": \"해당 3D 모델의 치수 정보를 찾을 수 없습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "치수 정보 수정", 
        description = """
            3D 모델의 치수 정보를 수정합니다.
            
            **인증 필요:** Bearer Token
            
            **요청 본문 예시:**
            ```json
            {
                "width": 55.0,
                "length": 48.5,
                "height": 95.0
            }
            ```
            
            **주의사항:**
            - null로 전달된 필드는 변경되지 않습니다.
            - 단위는 센티미터(cm)입니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("")
    ResponseEntity<ModelDimensionsResponseDto> updateModelDimensions(
            @Parameter(
                description = "수정할 치수 정보가 속한 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(
                description = "수정할 치수 정보",
                required = true
            )
            @Valid @RequestBody ModelDimensionsUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204", 
                description = "치수 정보 삭제 성공",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "치수 정보를 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "치수 정보 없음",
                        value = "{\"message\": \"해당 3D 모델의 치수 정보를 찾을 수 없습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "치수 정보 삭제", 
        description = """
            3D 모델의 치수 정보를 삭제합니다.
            
            **인증 필요:** Bearer Token
            
            **주의사항:**
            - 삭제된 치수 정보는 복구할 수 없습니다.
            - 3D 모델 자체는 삭제되지 않습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("")
    ResponseEntity<Void> deleteModelDimensions(
            @Parameter(
                description = "삭제할 치수 정보가 속한 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId
    );

}
