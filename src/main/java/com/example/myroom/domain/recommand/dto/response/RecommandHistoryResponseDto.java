package com.example.myroom.domain.recommand.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.recommand.model.RecommandHistory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "추천 이력 조회 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record RecommandHistoryResponseDto(

        @Schema(description = "추천 이력 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
        Long id,

        @Schema(description = "회원 ID", requiredMode = RequiredMode.REQUIRED, example = "10")
        Long memberId,

        @Schema(description = "처리 상태", requiredMode = RequiredMode.REQUIRED, example = "success")
        String status,

        @Schema(description = "방 분석 정보")
        RoomAnalysisResponseDto roomAnalysis,

        @Schema(description = "추천 정보")
        RecommendationResponseDto recommendation,

        @Schema(description = "AI 응답 timestamp(ms)", example = "1705312300000")
        Long responseTimestamp,

        @Schema(description = "생성 시각", example = "2026-03-14T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "수정 시각", example = "2026-03-14T10:30:00")
        LocalDateTime updatedAt
) {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static RecommandHistoryResponseDto from(RecommandHistory history) {
        return new RecommandHistoryResponseDto(
                history.getId(),
                history.getMember().getId(),
                history.getStatus(),
                toRoomAnalysis(history),
                toRecommendation(history),
                history.getResponseTimestamp(),
                history.getCreatedAt(),
                history.getUpdatedAt()
        );
    }

    private static RoomAnalysisResponseDto toRoomAnalysis(RecommandHistory history) {
        if (history.getRoomAnalysis() == null) {
            return null;
        }

        return new RoomAnalysisResponseDto(
                history.getRoomAnalysis().getStyle(),
                history.getRoomAnalysis().getColor(),
                history.getRoomAnalysis().getMaterial(),
                history.getRoomAnalysis().getDetectedFurnitures().stream()
                        .map(item -> item.getFurnitureName())
                        .toList(),
                history.getRoomAnalysis().getDetectedCount(),
                history.getRoomAnalysis().getDetailedDetections().stream()
                        .map(item -> new DetectedItemResponseDto(
                                item.getName(),
                                item.getConfidence(),
                                toBbox(item.getBboxX1(), item.getBboxY1(), item.getBboxX2(), item.getBboxY2())
                        ))
                        .toList()
        );
    }

    private static RecommendationResponseDto toRecommendation(RecommandHistory history) {
        if (history.getRecommendation() == null) {
            return null;
        }

        return new RecommendationResponseDto(
                history.getRecommendation().getTargetCategory(),
                history.getRecommendation().getReasoning(),
                history.getRecommendation().getSearchQuery(),
                history.getRecommendation().getResults().stream()
                        .map(result -> new RecommendationResultResponseDto(
                                result.getRankIndex(),
                                result.getScore(),
                                extractFurnitureType(result),
                                extractModel3dId(result),
                                extractImagePath(result),
                                extractFilename(result),
                                fromJson(result.getMetadataJson())
                        ))
                        .toList(),
                history.getRecommendation().getResultCount()
        );
    }

    private static Model3D extractPrimaryModel3d(com.example.myroom.domain.recommand.model.RecommandResult result) {
        if (result.getModel3Ds() == null || result.getModel3Ds().isEmpty()) {
            return null;
        }
        return result.getModel3Ds().get(0);
    }

    private static Long extractModel3dId(com.example.myroom.domain.recommand.model.RecommandResult result) {
        Model3D model3D = extractPrimaryModel3d(result);
        return model3D != null ? model3D.getId() : null;
    }

    private static String extractFurnitureType(com.example.myroom.domain.recommand.model.RecommandResult result) {
        Model3D model3D = extractPrimaryModel3d(result);
        return model3D != null && model3D.getFurnitureType() != null ? model3D.getFurnitureType().getDbValue() : null;
    }

    private static String extractImagePath(com.example.myroom.domain.recommand.model.RecommandResult result) {
        Model3D model3D = extractPrimaryModel3d(result);
        return model3D != null ? model3D.getThumbnailUrl() : null;
    }

    private static String extractFilename(com.example.myroom.domain.recommand.model.RecommandResult result) {
        Model3D model3D = extractPrimaryModel3d(result);
        if (model3D == null || model3D.getLink() == null || model3D.getLink().isBlank()) {
            return null;
        }
        String link = model3D.getLink();
        int slashIndex = link.lastIndexOf('/');
        if (slashIndex < 0 || slashIndex == link.length() - 1) {
            return link;
        }
        return link.substring(slashIndex + 1);
    }

    private static List<List<Double>> toBbox(Double x1, Double y1, Double x2, Double y2) {
        if (x1 == null || y1 == null || x2 == null || y2 == null) {
            return null;
        }
        return List.of(List.of(x1, y1), List.of(x2, y2));
    }

    private static Map<String, Object> fromJson(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank() || "null".equals(metadataJson)) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(metadataJson, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of("raw", metadataJson);
        }
    }

    @Schema(description = "방 분석 응답")
    @JsonNaming(SnakeCaseStrategy.class)
    public record RoomAnalysisResponseDto(
            String style,
            String color,
            String material,
            List<String> detectedFurniture,
            Integer detectedCount,
            List<DetectedItemResponseDto> detailedDetections
    ) {
    }

    @Schema(description = "상세 감지 항목 응답")
    @JsonNaming(SnakeCaseStrategy.class)
    public record DetectedItemResponseDto(
            String name,
            Double confidence,
            List<List<Double>> bbox
    ) {
    }

    @Schema(description = "추천 데이터 응답")
    @JsonNaming(SnakeCaseStrategy.class)
    public record RecommendationResponseDto(
            String targetCategory,
            String reasoning,
            String searchQuery,
            List<RecommendationResultResponseDto> results,
            Integer resultCount
    ) {
    }

    @Schema(description = "개별 추천 결과 응답")
    @JsonNaming(SnakeCaseStrategy.class)
    public record RecommendationResultResponseDto(
            Integer rank,
            Double score,
            String furnitureType,
            Long model3dId,
            String imagePath,
            String filename,
            Map<String, Object> metadata
    ) {
    }
}
