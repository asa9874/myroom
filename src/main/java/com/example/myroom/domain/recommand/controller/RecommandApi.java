package com.example.myroom.domain.recommand.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

@Tag(name = "🎯 가구 추천", description = "AI 기반 가구 추천 API - 방 이미지를 분석하여 어울리는 가구를 추천합니다. 결과는 WebSocket으로 실시간 전송됩니다.")
public interface RecommandApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "추천 요청 성공 - 비동기 처리 시작됨, 결과는 WebSocket으로 전송",
                content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                        name = "요청 성공",
                        value = "추천 요청이 접수되었습니다. 결과는 WebSocket을 통해 전송됩니다."
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 파일 누락 또는 유효하지 않은 파일 형식",
                content = @Content(
                    mediaType = "text/plain",
                    examples = {
                        @ExampleObject(
                            name = "파일 누락",
                            value = "이미지 파일을 선택해주세요."
                        ),
                        @ExampleObject(
                            name = "잘못된 형식",
                            value = "이미지 파일만 업로드 가능합니다."
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않음 - 유효하지 않은 토큰",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "서버 내부 오류",
                content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject(
                        name = "서버 오류",
                        value = "추천 요청 처리 중 오류가 발생했습니다: 상세 오류 메시지"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "가구 추천 요청", 
        description = """
            방 이미지를 업로드하여 AI 기반 가구 추천을 요청합니다.
            
            **인증 필요:** Bearer Token
            
            **처리 과정:**
            1. 이미지 파일 업로드 → S3 저장
            2. AI 서버로 추천 요청 발송 (RabbitMQ)
            3. AI가 방 스타일 분석 및 어울리는 가구 검색
            4. 결과를 WebSocket으로 실시간 전송
            
            **지원 이미지 형식:** JPG, JPEG, PNG
            
            **가구 카테고리 예시:**
            - `chair`: 의자
            - `table`: 테이블
            - `sofa`: 소파
            - `bed`: 침대
            - `lamp`: 조명
            - `desk`: 책상
            - `shelf`: 선반
            
            **WebSocket 연결:** `/ws/notifications` (STOMP 프로토콜)  
            **구독 토픽:** `/topic/recommand/{userId}`
            
            ### 📡 WebSocket 응답 예시
            
            #### 🎯 추천 성공 시
            ```json
            {
                "memberId": 1,
                "status": "success",
                "timestamp": 1705312300000,
                "roomAnalysis": {
                    "style": "modern",
                    "color": "neutral",
                    "material": "wood",
                    "detectedFurniture": ["sofa", "table", "lamp"],
                    "detectedCount": 3,
                    "detailedDetections": [
                        {
                            "name": "sofa",
                            "confidence": 0.95,
                            "bbox": [[100.0, 150.0], [400.0, 350.0]]
                        }
                    ]
                },
                "recommendation": {
                    "targetCategory": "chair",
                    "reasoning": "모던한 인테리어에 어울리는 심플한 디자인의 의자를 추천합니다.",
                    "searchQuery": "modern minimalist chair wooden legs",
                    "results": [
                        {
                            "rank": 1,
                            "score": 0.95,
                            "furnitureType": "chair",
                            "model3dId": 123,
                            "imagePath": "https://s3.amazonaws.com/myroom-bucket/thumbnails/chair_123.png",
                            "filename": "modern_chair.glb",
                            "metadata": {}
                        }
                    ],
                    "resultCount": 5
                }
            }
            ```
            
            #### ❌ 추천 실패 시
            ```json
            {
                "memberId": 1,
                "status": "failed",
                "timestamp": 1705312300000,
                "roomAnalysis": null,
                "recommendation": null
            }
            ```
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> requestRecommandation(
            @Parameter(
                description = "분석할 방 이미지 파일 (JPG, PNG 지원, 최대 10MB)",
                required = true
            )
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            
            @Parameter(
                description = "추천받을 가구 카테고리 (chair, table, sofa, bed, lamp, desk, shelf 등). 생략하거나 빈 문자열 전달 시 카테고리 무관 전체 반환.",
                required = false,
                example = "",
                schema = @Schema(defaultValue = "", allowableValues = {"", "chair", "table", "sofa", "bed", "lamp", "desk", "shelf"})
            )
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            
            @Parameter(
                description = "반환받을 추천 결과 개수 (1~100, 기본값: 5)",
                required = false,
                example = "5",
                schema = @Schema(defaultValue = "5", minimum = "1", maximum = "100")
            )
            @RequestParam(value = "topK", required = false, defaultValue = "5") Integer topK,
            
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );
}