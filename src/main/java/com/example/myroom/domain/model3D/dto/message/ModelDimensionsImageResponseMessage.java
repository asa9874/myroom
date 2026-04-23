package com.example.myroom.domain.model3D.dto.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "치수 추출 AI 응답 메시지")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDimensionsImageResponseMessage {

    @Schema(description = "3D 모델 ID", requiredMode = RequiredMode.NOT_REQUIRED, example = "1")
    @JsonProperty("model3d_id")
    @JsonAlias({"model3dId", "model_id", "modelId"})
    private Long model3dId;

    @Schema(description = "요청 회원 ID", requiredMode = RequiredMode.NOT_REQUIRED, example = "10")
    @JsonProperty("member_id")
    @JsonAlias("memberId")
    private Long memberId;

    @Schema(description = "처리 상태", requiredMode = RequiredMode.NOT_REQUIRED, example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED"})
    private String status;

    @Schema(description = "상태 메시지", requiredMode = RequiredMode.NOT_REQUIRED, example = "completed")
    private String message;

    @Schema(description = "응답 시각 (ms)", requiredMode = RequiredMode.NOT_REQUIRED, example = "1700000000000")
    private Long timestamp;

    @Schema(description = "추출 치수 정보")
    private Dimensions dimensions;

    @Schema(description = "치수 객체")
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dimensions {

        @Schema(description = "가로", requiredMode = RequiredMode.NOT_REQUIRED, example = "120.5")
        private Double width;

        @Schema(description = "세로/깊이", requiredMode = RequiredMode.NOT_REQUIRED, example = "60.2")
        @JsonProperty("depth")
        @JsonAlias("length")
        private Double depth;

        @Schema(description = "높이", requiredMode = RequiredMode.NOT_REQUIRED, example = "75.0")
        private Double height;

        @Schema(description = "단위", requiredMode = RequiredMode.NOT_REQUIRED, example = "cm")
        private String unit;
    }
}
