package com.example.myroom.domain.model3D.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.model3D.dto.request.Model3DCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestV2Dto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestV3Dto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

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

@Tag(name = "🏠 3D 모델", description = "사용자의 3D 모델 관리 및 조회 API - 3D 가구 모델의 업로드, 조회, 수정, 삭제 기능을 제공합니다.")
public interface Model3DApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "3D 모델 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Model3DResponseDto.class),
                    examples = @ExampleObject(
                        name = "3D 모델 조회 응답",
                        value = """
                            {
                                "id": 1,
                                "name": "모던 의자",
                                "created_at": "2024-01-15T10:30:00",
                                "link": "https://s3.amazonaws.com/myroom-bucket/models/chair.glb",
                                "creator_id": 1,
                                "is_shared": false,
                                "description": "모던 스타일의 회색 의자입니다.",
                                "thumbnail_url": "https://s3.amazonaws.com/myroom-bucket/thumbnails/chair_thumb.png",
                                "is_vector_db_trained": true,
                                "status": "SUCCESS"
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "접근 권한 없음 - 다른 사용자의 비공개 모델", 
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
        summary = "3D 모델 단건 조회", 
        description = """
            3D 모델 ID로 특정 모델의 상세 정보를 조회합니다.
            
            **인증 필요:** Bearer Token
            
            **접근 권한:**
            - 자신이 생성한 모델: 항상 조회 가능
            - 다른 사용자의 모델: 공유(is_shared=true) 상태인 경우만 조회 가능
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{model3dId}")
    ResponseEntity<Model3DResponseDto> getModel3DById(
            @Parameter(
                description = "조회할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "3D 모델 수정 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Model3DResponseDto.class),
                    examples = @ExampleObject(
                        name = "수정 성공 응답",
                        value = """
                            {
                                "id": 1,
                                "name": "모던 소파",
                                "created_at": "2024-01-15T10:30:00",
                                "link": "https://s3.amazonaws.com/myroom-bucket/models/chair.glb",
                                "creator_id": 1,
                                "is_shared": true,
                                "description": "편안한 3인용 소파입니다.",
                                "thumbnail_url": "https://s3.amazonaws.com/myroom-bucket/thumbnails/chair_thumb.png",
                                "is_vector_db_trained": true,
                                "status": "SUCCESS"
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
                description = "수정 권한 없음", 
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
        summary = "3D 모델 수정", 
        description = """
            자신이 생성한 3D 모델의 정보를 수정합니다.
            
            **인증 필요:** Bearer Token
            
            **요청 본문 예시:**
            ```json
            {
                "name": "모던 소파",
                "is_shared": true,
                "description": "편안한 3인용 소파입니다."
            }
            ```
            
            **주의사항:**
            - 자신이 생성한 모델만 수정 가능합니다.
            - null로 전달된 필드는 변경되지 않습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{model3dId}")
    ResponseEntity<Model3DResponseDto> updateModel3D(
            @Parameter(
                description = "수정할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "수정할 모델 정보", required = true)
            @Valid @RequestBody Model3DUpdateRequestDto updateRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @Operation(
        summary = "3D 모델 정보 수정 v2",
        description = """
            자신이 생성한 3D 모델의 정보를 수정합니다. (link 필드 포함)
            
            **인증 필요:** Bearer Token
            
            **요청 본문 예시:**
            ```json
            {
                "name": "모던 소파",
                "is_shared": true,
                "description": "편안한 3인용 소파입니다.",
                "link": "https://example.com/model3d/sofa.glb"
            }
            ```
            
            **주의사항:**
            - 자신이 생성한 모델만 수정 가능합니다.
            - null로 전달된 필드는 변경되지 않습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/v2/{model3dId}")
    ResponseEntity<Model3DResponseDto> updateModel3Dv2(
            @Parameter(
                description = "수정할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "수정할 모델 정보 (link 포함)", required = true)
            @Valid @RequestBody Model3DUpdateRequestV2Dto updateRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @Operation(
        summary = "3D 모델 정보 수정 v3",
        description = """
            자신이 생성한 3D 모델의 정보를 수정합니다. (furnitureType 포함, link 제거)
            
            **인증 필요:** Bearer Token
            
            **요청 본문 예시:**
            ```json
            {
                "name": "모던 소파",
                "is_shared": true,
                "description": "편안한 3인용 소파입니다.",
                "furniture_type": "sofa"
            }
            ```
            
            **주의사항:**
            - 자신이 생성한 모델만 수정 가능합니다.
            - null로 전달된 필드는 변경되지 않습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/v3/{model3dId}")
    ResponseEntity<Model3DResponseDto> updateModel3Dv3(
            @Parameter(
                description = "수정할 3D 모델의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(description = "수정할 모델 정보 (furnitureType 포함)", required = true)
            @Valid @RequestBody Model3DUpdateRequestV3Dto updateRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
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
                description = "삭제 권한 없음", 
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
        summary = "3D 모델 삭제", 
        description = """
            자신이 생성한 3D 모델을 삭제합니다.
            
            **인증 필요:** Bearer Token
            
            **주의사항:**
            - 자신이 생성한 모델만 삭제 가능합니다.
            - 삭제된 모델은 복구할 수 없습니다.
            - 모델 파일(S3)도 함께 삭제됩니다.
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
            @PathVariable(name = "model3dId") Long model3dId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "전체 3D 모델 삭제 성공",
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "내 3D 모델 전체 삭제", 
        description = """
            현재 로그인한 사용자의 모든 3D 모델을 삭제합니다.
            
            **인증 필요:** Bearer Token
            
            **주의사항:**
            - 모든 모델이 영구 삭제됩니다.
            - 삭제된 모델은 복구할 수 없습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping
    ResponseEntity<Void> deleteAllModel3Ds(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "파일 업로드 및 3D 모델 생성 요청 성공 - 처리 중 상태로 생성됨",
                content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                        name = "업로드 성공 응답",
                        value = "3D 모델 생성이 요청되었습니다. 모델 ID: 1"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 파일 형식 오류 또는 필수 필드 누락", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "이미지로 3D 모델 생성 요청", 
        description = """
            이미지 파일을 업로드하여 AI 기반 3D 모델 생성을 요청합니다.
            
            **인증 필요:** Bearer Token
            
            **지원 이미지 형식:** JPG, JPEG, PNG
            
            **가구 유형 (furniture_type):**
            - `chair`: 의자
            - `table`: 테이블
            - `bed`: 침대
            - `sofa`: 소파
            - 기타 가구 유형
            
            **처리 과정:**
            1. 이미지 업로드 → S3 저장
            2. 3D 모델 생성 요청 (PROCESSING 상태)
            3. AI 서버에서 3D 모델 생성
            4. 완료 시 WebSocket으로 알림 (SUCCESS/FAILED 상태)
            
            ### 📡 WebSocket 알림 정보
            
            **WebSocket 연결:** `/ws/notifications` (STOMP 프로토콜)  
            **구독 토픽:** `/topic/model3d/{userId}` (개인 알림)  
            **브로드캐스트 토픽:** `/topic/model3d/all` (전체 알림)
            
            #### 🎯 생성 성공 시 WebSocket 응답
            ```json
            {
                "notificationType": "MODEL_GENERATION_SUCCESS",
                "memberId": 1,
                "originalImageUrl": "https://s3.amazonaws.com/myroom-bucket/images/chair_original.jpg",
                "model3dUrl": "https://s3.amazonaws.com/myroom-bucket/models/chair.glb",
                "thumbnailUrl": "https://s3.amazonaws.com/myroom-bucket/thumbnails/chair_thumb.png",
                "status": "SUCCESS",
                "message": "3D 모델이 성공적으로 생성되었습니다.",
                "processingTimeSeconds": 45,
                "timestamp": 1705312300000
            }
            ```
            
            #### ❌ 생성 실패 시 WebSocket 응답
            ```json
            {
                "notificationType": "MODEL_GENERATION_FAILED",
                "memberId": 1,
                "originalImageUrl": "https://s3.amazonaws.com/myroom-bucket/images/chair_original.jpg",
                "model3dUrl": null,
                "thumbnailUrl": null,
                "status": "FAILED",
                "message": "이미지에서 가구를 인식할 수 없어 3D 모델 생성에 실패했습니다.",
                "processingTimeSeconds": 30,
                "timestamp": 1705312300000
            }
            ```
            
            #### ⚙️ 처리 중 상태 WebSocket 응답 (선택적)
            ```json
            {
                "notificationType": "MODEL_GENERATION_PROGRESS",
                "memberId": 1,
                "originalImageUrl": "https://s3.amazonaws.com/myroom-bucket/images/chair_original.jpg",
                "model3dUrl": null,
                "thumbnailUrl": null,
                "status": "PROCESSING",
                "message": "3D 모델을 생성 중입니다. 잠시만 기다려주세요...",
                "processingTimeSeconds": null,
                "timestamp": 1705312250000
            }
            ```
            
            **주의사항:**
            - 모델 생성은 비동기로 처리됩니다.
            - 생성 완료 알림은 WebSocket을 통해 받을 수 있습니다.
            - 처리 시간은 이미지 복잡도에 따라 30초~5분 소요될 수 있습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadModel3DFile(
            @Parameter(
                description = "3D 모델 생성에 사용할 가구 이미지 파일 (JPG, PNG 지원)",
                required = true
            )
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @Parameter(
                description = "가구 유형 (chair, table, bed, sofa 등)",
                required = true,
                example = "chair"
            )
            @RequestParam(value = "furniture_type", required = true) String furnitureType,
            @Parameter(
                description = "3D 모델 이름",
                required = true,
                example = "모던 의자"
            )
            @RequestParam(value = "name", required = true) String name,
            @Parameter(
                description = "모델 설명",
                required = false,
                example = "모던 스타일의 회색 의자입니다."
            )
            @RequestParam(value = "description", required = false) String description,
            @Parameter(
                description = "공유 여부 (true: 공개, false: 비공개)",
                required = false,
                example = "false"
            )
            @RequestParam(value = "is_shared", required = false) Boolean isShared,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "간편 파일 업로드 및 3D 모델 생성 요청 성공 - 처리 중 상태로 생성됨",
                content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                        name = "간편 업로드 성공 응답",
                        value = "3D 모델 생성이 요청되었습니다. 모델 ID: 1"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 파일 형식 오류", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "간편 이미지 업로드로 3D 모델 생성", 
        description = """
            이미지 파일만으로 간단하게 AI 기반 3D 모델 생성을 요청합니다.
            
            **인증 필요:** Bearer Token
            
            **지원 이미지 형식:** JPG, JPEG, PNG
            
            **자동 설정값:**
            - **가구 유형:** `temp` (임시)
            - **모델 이름:** `Temp_{타임스탬프}` (자동 생성)
            - **설명:** "임시 업로드된 3D 모델"
            - **공유 여부:** `false` (비공개)
            
            **사용 용도:**
            - 빠른 테스트 업로드
            - 임시 3D 모델 생성
            - 상세 정보 입력 없이 즉시 생성
            
            **처리 과정:**
            1. 이미지 업로드 → S3 저장
            2. 기본값으로 3D 모델 생성 요청 (PROCESSING 상태)
            3. AI 서버에서 3D 모델 생성
            4. 완료 시 WebSocket으로 알림 (SUCCESS/FAILED 상태)
            
            ### 📡 WebSocket 알림 정보
            
            **WebSocket 연결:** `/ws/notifications` (STOMP 프로토콜)  
            **구독 토픽:** `/topic/model3d/{userId}` (개인 알림)  
            **브로드캐스트 토픽:** `/topic/model3d/all` (전체 알림)
            
            **주의사항:**
            - 모델 생성은 비동기로 처리됩니다.
            - 생성 완료 알림은 WebSocket을 통해 받을 수 있습니다.
            - 생성된 임시 모델은 나중에 수정할 수 있습니다.
            - 처리 시간은 이미지 복잡도에 따라 30초~5분 소요될 수 있습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/upload-simple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadSimpleModel3DFile(
            @Parameter(
                description = "3D 모델 생성에 사용할 가구 이미지 파일 (JPG, PNG 지원)",
                required = true
            )
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "전체 3D 모델 조회 성공 (관리자 전용)",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Model3DResponseDto.class))
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
            시스템의 모든 3D 모델을 조회합니다.
            
            **인증 필요:** Bearer Token (ADMIN 권한)
            
            **권한:** 관리자 전용
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    ResponseEntity<List<Model3DResponseDto>> getAllModel3Ds(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "회원별 3D 모델 검색 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "접근 권한 없음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "회원별 3D 모델 검색", 
        description = """
            특정 회원의 3D 모델을 이름으로 검색합니다. (페이지네이션 지원)
            
            **인증 필요:** Bearer Token
            
            **페이지네이션 파라미터:**
            - `page`: 페이지 번호 (0부터 시작)
            - `size`: 페이지당 항목 수
            - `sort`: 정렬 기준 (예: createdAt,desc)
            
            **검색:**
            - `name` 파라미터로 모델 이름 검색 (부분 일치)
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/member/{memberId}/search")
    ResponseEntity<Page<Model3DResponseDto>> getModel3DsByMemberId(
            @Parameter(
                description = "조회할 회원의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "memberId") Long memberId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member,
            @Parameter(
                description = "검색할 모델 이름 (부분 일치)",
                required = false,
                example = "의자"
            )
            @RequestParam(required = false, name = "name") String name,
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "공유된 3D 모델 검색 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Page.class)
                )
            )
        }
    )
    @Operation(
        summary = "공유된 3D 모델 검색", 
        description = """
            공유 상태(is_shared=true)인 모든 3D 모델을 검색합니다. (페이지네이션 지원)
            
            **인증 필요:** Bearer Token
            
            **페이지네이션 파라미터:**
            - `page`: 페이지 번호 (0부터 시작)
            - `size`: 페이지당 항목 수
            - `sort`: 정렬 기준 (예: createdAt,desc)
            
            **검색:**
            - `name` 파라미터로 모델 이름 검색 (부분 일치)
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/shared/search")
    ResponseEntity<Page<Model3DResponseDto>> getSharedModel3Ds(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member,
            @Parameter(
                description = "검색할 모델 이름 (부분 일치)",
                required = false,
                example = "소파"
            )
            @RequestParam(required = false, name = "name") String name,
            @Parameter(description = "페이지네이션 정보")
            Pageable pageable
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "미학습 3D 모델 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Model3DResponseDto.class))
                )
            )
        }
    )
    @Operation(
        summary = "VectorDB 미학습 3D 모델 조회", 
        description = """
            VectorDB에 아직 학습되지 않은 3D 모델 목록을 조회합니다.
            
            **용도:** AI 추천 시스템을 위한 학습 대상 모델 확인
            
            **반환:** is_vector_db_trained=false인 모델 목록
            """
    )
    @GetMapping("/untrained")
    ResponseEntity<List<Model3DResponseDto>> getNotVectorDbTrainedModel3Ds();

}
