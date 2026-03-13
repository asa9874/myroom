package com.example.myroom.domain.recommand.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.recommand.model.RecommandHistory;
import com.example.myroom.domain.recommand.model.RecommandResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "추천 이력 간소화 조회 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record RecommandSimpleHistoryResponseDto(

        @Schema(description = "추천 이력 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
        Long id,

        @Schema(description = "회원 ID", requiredMode = RequiredMode.REQUIRED, example = "10")
        Long memberId,

        @Schema(description = "처리 상태", requiredMode = RequiredMode.REQUIRED, example = "success")
        String status,

        @Schema(description = "방 스타일", example = "modern")
        String roomStyle,

        @Schema(description = "방 색상", example = "neutral")
        String roomColor,

        @Schema(description = "방 재질", example = "wood")
        String roomMaterial,

        @Schema(description = "추천 결과 간소화 목록")
        List<SimpleRecommendationItemDto> results,

        @Schema(description = "생성 시각", example = "2026-03-14T10:30:00")
        LocalDateTime createdAt
) {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static RecommandSimpleHistoryResponseDto from(RecommandHistory history) {
        return new RecommandSimpleHistoryResponseDto(
                history.getId(),
                history.getMember().getId(),
                history.getStatus(),
                history.getRoomAnalysis() != null ? history.getRoomAnalysis().getStyle() : null,
                history.getRoomAnalysis() != null ? history.getRoomAnalysis().getColor() : null,
                history.getRoomAnalysis() != null ? history.getRoomAnalysis().getMaterial() : null,
                history.getRecommendation() != null
                        ? history.getRecommendation().getResults().stream().map(RecommandSimpleHistoryResponseDto::toSimpleItem).toList()
                        : List.of(),
                history.getCreatedAt()
        );
    }

    private static SimpleRecommendationItemDto toSimpleItem(RecommandResult result) {
        Model3D model3D = extractPrimaryModel3d(result);
        Map<String, Object> metadata = parseMetadata(result.getMetadataJson());

        return new SimpleRecommendationItemDto(
                model3D != null ? model3D.getId() : null,
                model3D != null && model3D.getFurnitureType() != null ? model3D.getFurnitureType().getDbValue() : null,
                model3D != null ? model3D.getThumbnailUrl() : null,
                extractString(metadata, "name", model3D != null ? model3D.getName() : null),
                extractString(metadata, "description", model3D != null ? model3D.getDescription() : null),
                extractLong(metadata, "member_id", model3D != null ? model3D.getCreatorId() : null)
        );
    }

    private static Model3D extractPrimaryModel3d(RecommandResult result) {
        if (result.getModel3Ds() == null || result.getModel3Ds().isEmpty()) {
            return null;
        }
        return result.getModel3Ds().get(0);
    }

    private static Map<String, Object> parseMetadata(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank() || "null".equals(metadataJson)) {
            return Map.of();
        }

        try {
            return OBJECT_MAPPER.readValue(metadataJson, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private static String extractString(Map<String, Object> metadata, String key, String fallback) {
        Object value = metadata.get(key);
        return value != null ? String.valueOf(value) : fallback;
    }

    private static Long extractLong(Map<String, Object> metadata, String key, Long fallback) {
        Object value = metadata.get(key);
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Schema(description = "추천 결과 간소화 항목")
    @JsonNaming(SnakeCaseStrategy.class)
    public record SimpleRecommendationItemDto(
            Long model3dId,
            String furnitureType,
            String imagePath,
            String name,
            String description,
            Long memberId
    ) {
    }
}
