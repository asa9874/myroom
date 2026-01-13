package com.example.myroom.domain.recommand.dto.message;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추천 응답 메시지
 * - 외부 AI 서버에서 받은 추천 분석 결과를 담는 메시지입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommandResponseMessage {
    
    /**
     * 요청한 회원 ID
     */
    private Long memberId;
    
    /**
     * 처리 상태 (success, failed 등)
     */
    private String status;
    
    /**
     * 방 분석 정보
     */
    private RoomAnalysis roomAnalysis;
    
    /**
     * 가구 추천 정보
     */
    private RecommendationData recommendation;
    
    /**
     * 응답 수신 시각 (Unix timestamp, milliseconds)
     */
    private Long timestamp;
    
    /**
     * 방 분석 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomAnalysis {
        private String style;
        private String color;
        private String material;
        
        @JsonProperty("detected_furniture")
        @JsonAlias("detectedFurniture")
        private List<String> detectedFurniture;
        
        @JsonProperty("detected_count")
        @JsonAlias("detectedCount")
        private Integer detectedCount;
        
        @JsonProperty("detailed_detections")
        @JsonAlias("detailedDetections")
        private List<DetectedItem> detailedDetections;
    }
    
    /**
     * 감지된 가구 항목
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetectedItem {
        private String name;
        private Double confidence;
        private List<List<Double>> bbox;
    }
    
    /**
     * 추천 데이터
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationData {
        @JsonProperty("target_category")
        @JsonAlias("targetCategory")
        private String targetCategory;
        
        private String reasoning;
        
        @JsonProperty("search_query")
        @JsonAlias("searchQuery")
        private String searchQuery;
        
        private List<RecommendationResult> results;
        
        @JsonProperty("result_count")
        @JsonAlias("resultCount")
        private Integer resultCount;
    }
    
    /**
     * 추천 결과 항목
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationResult {
        private Integer rank;
        private Double score;
        
        @JsonProperty("furniture_type")
        @JsonAlias("furnitureType")
        private String furnitureType;
        
        @JsonProperty("image_path")
        @JsonAlias("imagePath")
        private String imagePath;
        
        private String filename;
        private Map<String, Object> metadata;
    }
}
