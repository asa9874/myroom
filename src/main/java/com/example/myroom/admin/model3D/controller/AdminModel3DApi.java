package com.example.myroom.admin.model3D.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "⚙️ 관리자 - 3D 모델", description = "관리자 전용 3D 모델 관리 API - 시스템 내 모든 3D 모델의 CRUD 및 소유자 변경 기능을 제공합니다. (ADMIN 권한 필요)")
public interface AdminModel3DApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "3D 모델 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdminModel3DResponseDto.class),
                    examples = @ExampleObject(
                        name = "관리자 3D 모델 조회 응답",
                        value = """
                            {
                                "id": 1,
                                "name": "모던 의자",
                                "created_at": "2024-01-15T10:30:00",
                                "link": "https://s3.amazonaws.com/myroom-bucket/models/chair.glb",
                                "creator_id": 1,
                                "is_shared": true,
                                "description": "모던 스타일의 회색 의자입니다."
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "관리자 권한 필요", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "권한 없음",
                        value = "{\"message\": \"관리자 권한이 필요합니다.\"}"
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
            )
        }
    )
    @Operation(
        summary = "3D 모델 단건 조회 (관리자)", 
        description = """
            관리자가 특정 3D 모델의 정보를 조회합니다.
            
            **인증 필요:** Bearer Token (ADMIN 권한)
            
            **응답 예시:**
            ```json
            {
                "id": 1,
                "name": "모던 의자",
                "created_at": "2024-01-15T10:30:00",
                "link": "https://s3.amazonaws.com/myroom-bucket/models/chair.glb",
                "creator_id": 1,
                "is_shared": true,
                "description": "모던 스타일의 회색 의자입니다."
            }
            ```
            
            **특징:**
            - 관리자는 모든 사용자의 3D 모델을 조회할 수 있습니다.
            - 공개/비공개 상태와 관계없이 조회 가능합니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{model3dId}")
    ResponseEntity<AdminModel3DResponseDto> getModel3DById(
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
                responseCode = "200", 
                description = "전체 3D 모델 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AdminModel3DResponseDto.class)),
                    examples = @ExampleObject(
                        name = "전체 모델 목록 응답",
                        value = """
                            [
                                {
                                    "id": 1,
                                    "name": "모던 의자",
                                    "created_at": "2024-01-15T10:30:00",
                                    "link": "https://s3.amazonaws.com/myroom-bucket/models/chair.glb",
                                    "creator_id": 1,
                                    "is_shared": true,
                                    "description": "모던 스타일의 회색 의자입니다."
                                },
                                {
                                    "id": 2,
                                    "name": "원목 테이블",
                                    "created_at": "2024-01-16T14:20:00",
                                    "link": "https://s3.amazonaws.com/myroom-bucket/models/table.glb",
                                    "creator_id": 2,
                                    "is_shared": false,
                                    "description": "원목 소재의 4인용 테이블입니다."
                                }
                            ]
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "관리자 권한 필요", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "전체 3D 모델 조회 (관리자)", 
        description = """
            관리자가 시스템의 모든 3D 모델 목록을 조회합니다.
            
            **인증 필요:** Bearer Token (ADMIN 권한)
            
            **특징:**
            - 모든 사용자의 3D 모델을 조회합니다.
            - 공개/비공개 상태와 관계없이 모든 모델이 표시됩니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/")
    ResponseEntity<List<AdminModel3DResponseDto>> getAllModel3D();

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "3D 모델 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdminModel3DResponseDto.class),
                    examples = @ExampleObject(
                        name = "생성 성공 응답",
                        value = """
                            {
                                "id": 3,
                                "name": "럭셔리 소파",
                                "created_at": "2024-01-17T09:00:00",
                                "link": "https://s3.amazonaws.com/myroom-bucket/models/sofa.glb",
                                "creator_id": 1,
                                "is_shared": true,
                                "description": "관리자가 생성한 럭셔리 소파입니다."
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
                        value = "{\"message\": \"이름은 비어 있을 수 없습니다.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "관리자 권한 필요", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "생성자 회원을 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "회원 없음",
                        value = "{\"message\": \"해당 생성자를 찾을 수 없습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "3D 모델 생성 (관리자)", 
        description = """
            관리자가 새로운 3D 모델을 생성합니다.
            
            **인증 필요:** Bearer Token (ADMIN 권한)
            
            **요청 본문 예시:**
            ```json
            {
                "name": "럭셔리 소파",
                "link": "https://s3.amazonaws.com/myroom-bucket/models/sofa.glb",
                "creator_id": 1,
                "is_shared": true,
                "description": "관리자가 생성한 럭셔리 소파입니다."
            }
            ```
            
            **특징:**
            - 관리자가 다른 사용자를 생성자(creator_id)로 지정하여 모델을 생성할 수 있습니다.
            - 공유 상태를 직접 설정할 수 있습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/")
    ResponseEntity<AdminModel3DResponseDto> createModel3D(
            @Parameter(
                description = "생성할 3D 모델 정보",
                required = true
            )
            @Valid @RequestBody AdminModel3DCreateRequestDto createRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "3D 모델 수정 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AdminModel3DResponseDto.class),
                    examples = @ExampleObject(
                        name = "수정 성공 응답",
                        value = """
                            {
                                "id": 1,
                                "name": "수정된 모던 의자",
                                "created_at": "2024-01-15T10:30:00",
                                "link": "https://s3.amazonaws.com/myroom-bucket/models/chair_v2.glb",
                                "creator_id": 2,
                                "is_shared": false,
                                "description": "수정된 설명입니다."
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청", 
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "관리자 권한 필요", 
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
        summary = "3D 모델 수정 (관리자)", 
        description = """
            관리자가 특정 3D 모델을 수정합니다.
            
            **인증 필요:** Bearer Token (ADMIN 권한)
            
            **요청 본문 예시:**
            ```json
            {
                "name": "수정된 모던 의자",
                "link": "https://s3.amazonaws.com/myroom-bucket/models/chair_v2.glb",
                "creator_id": 2,
                "is_shared": false,
                "description": "수정된 설명입니다."
            }
            ```
            
            **특징:**
            - 관리자는 모든 사용자의 3D 모델을 수정할 수 있습니다.
            - 생성자(creator_id)를 다른 사용자로 변경할 수 있습니다.
            - null로 전달된 필드는 변경되지 않습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{model3dId}")
    ResponseEntity<AdminModel3DResponseDto> updateModel3D(
            @Parameter(
                description = "수정할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(
                description = "수정할 3D 모델 정보",
                required = true
            )
            @Valid @RequestBody AdminModel3DUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "3D 모델 삭제 성공",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "관리자 권한 필요", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "권한 없음",
                        value = "{\"message\": \"관리자 권한이 필요합니다.\"}"
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
            )
        }
    )
    @Operation(
        summary = "3D 모델 삭제 (관리자)", 
        description = """
            관리자가 특정 3D 모델을 삭제합니다.
            
            **인증 필요:** Bearer Token (ADMIN 권한)
            
            **특징:**
            - 관리자는 모든 사용자의 3D 모델을 삭제할 수 있습니다.
            - 삭제된 모델은 복구할 수 없습니다.
            - S3에 저장된 모델 파일도 함께 삭제됩니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{model3dId}")
    ResponseEntity<Void> deleteModel3D(
            @Parameter(
                description = "삭제할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId
    );

}
