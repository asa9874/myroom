package com.example.myroom.domain.recommand.dto.message;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추천 응답 메시지
 * - 외부 AI 서버에서 받은 추천 분석 결과를 담는 메시지입니다.
 */
@Schema(description = "AI 가구 추천 응답 메시지 - WebSocket을 통해 클라이언트로 전송되는 추천 결과")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommandResponseMessage {

    /**
     * 요청한 회원 ID
     */
    @Schema(
        description = "추천을 요청한 회원의 고유 ID", 
        requiredMode = RequiredMode.REQUIRED,
        example = "1"
    )
    private Long memberId;

    /**
     * 처리 상태 (success, failed 등)
     */
    @Schema(
        description = "추천 처리 상태 (success: 성공, failed: 실패)", 
        requiredMode = RequiredMode.REQUIRED,
        example = "success",
        allowableValues = {"success", "failed"}
    )
    private String status;

    /**
     * 방 분석 정보
     */
    @Schema(description = "AI가 분석한 방 정보 (스타일, 색상, 재질, 감지된 가구 등)")
    private RoomAnalysis roomAnalysis;

    /**
     * 가구 추천 정보
     */
    @Schema(description = "AI가 추천하는 가구 정보")
    private RecommendationData recommendation;

    /**
     * 응답 수신 시각 (Unix timestamp, milliseconds)
     */
    @Schema(
        description = "응답 수신 시각 (Unix timestamp, milliseconds)", 
        requiredMode = RequiredMode.REQUIRED,
        example = "1705312300000"
    )
    private Long timestamp;

    /**
     * 방 분석 정보
     */
    @Schema(description = "방 분석 결과 - AI가 이미지에서 분석한 방의 스타일, 색상, 재질 및 감지된 가구 정보")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomAnalysis {
        @Schema(
            description = "방의 인테리어 스타일 (modern, classic, minimalist, scandinavian 등)", 
            example = "modern"
        )
        private String style;
        
        @Schema(
            description = "방의 주요 색상 톤 (warm, cool, neutral 등)", 
            example = "neutral"
        )
        private String color;
        
        @Schema(
            description = "방에서 감지된 주요 재질 (wood, metal, fabric 등)", 
            example = "wood"
        )
        private String material;

        @Schema(
            description = "감지된 가구 유형 목록", 
            example = "[\"sofa\", \"table\", \"lamp\"]"
        )
        @JsonProperty("detected_furniture")
        @JsonAlias("detectedFurniture")
        private List<String> detectedFurniture;

        @Schema(
            description = "감지된 가구의 총 개수", 
            example = "3"
        )
        @JsonProperty("detected_count")
        @JsonAlias("detectedCount")
        private Integer detectedCount;

        @Schema(description = "상세 감지 정보 목록 (가구별 신뢰도 및 위치 정보 포함)")
        @JsonProperty("detailed_detections")
        @JsonAlias("detailedDetections")
        private List<DetectedItem> detailedDetections;
    }

    /**
     * 감지된 가구 항목
     */
    @Schema(description = "감지된 개별 가구 항목 정보")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectedItem {
        @Schema(
            description = "감지된 가구의 이름", 
            example = "chair"
        )
        private String name;
        
        @Schema(
            description = "감지 신뢰도 (0.0 ~ 1.0)", 
            example = "0.95"
        )
        private Double confidence;
        
        @Schema(
            description = "이미지 내 가구 위치 (바운딩 박스 좌표 [[x1,y1], [x2,y2]])", 
            example = "[[100.0, 150.0], [300.0, 400.0]]"
        )
        private List<List<Double>> bbox;
    }

    /**
     * 추천 데이터
     */
    @Schema(description = "가구 추천 결과 데이터")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationData {
        @Schema(
            description = "추천 대상 가구 카테고리", 
            example = "chair"
        )
        @JsonProperty("target_category")
        @JsonAlias("targetCategory")
        private String targetCategory;

        @Schema(
            description = "AI의 추천 이유 설명", 
            example = "모던한 인테리어에 어울리는 심플한 디자인의 의자를 추천합니다."
        )
        private String reasoning;

        @Schema(
            description = "AI가 사용한 검색 쿼리", 
            example = "modern minimalist chair wooden legs"
        )
        @JsonProperty("search_query")
        @JsonAlias("searchQuery")
        private String searchQuery;

        @Schema(description = "추천 결과 목록 (유사도 점수 순)")
        private List<RecommendationResult> results;

        @Schema(
            description = "반환된 추천 결과 개수", 
            example = "5"
        )
        @JsonProperty("result_count")
        @JsonAlias("resultCount")
        private Integer resultCount;
    }

    /**
     * 추천 결과 항목
     */
    @Schema(description = "개별 추천 가구 항목")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationResult {
        @Schema(
            description = "추천 순위 (1부터 시작)", 
            example = "1"
        )
        private Integer rank;
        
        @Schema(
            description = "유사도 점수 (0.0 ~ 1.0, 높을수록 적합)", 
            example = "0.95"
        )
        private Double score;

        @Schema(
            description = "추천된 가구의 유형", 
            example = "chair"
        )
        @JsonProperty("furniture_type")
        @JsonAlias("furnitureType")
        private String furnitureType;

        @Schema(
            description = "추천된 3D 모델의 고유 ID", 
            example = "123"
        )
        @JsonProperty("model3d_id")
        @JsonAlias("model3dId")
        private Long model3dId;

        @Schema(
            description = "추천된 가구의 이미지 경로", 
            example = "https://s3.amazonaws.com/myroom-bucket/thumbnails/chair_123.png"
        )
        @JsonProperty("image_path")
        @JsonAlias("imagePath")
        private String imagePath;

        @Schema(
            description = "추천된 가구의 파일명", 
            example = "modern_chair.glb"
        )
        private String filename;
        
        @Schema(description = "추가 메타데이터 (가구 상세 정보 등)")
        private Map<String, Object> metadata;
    }
}
