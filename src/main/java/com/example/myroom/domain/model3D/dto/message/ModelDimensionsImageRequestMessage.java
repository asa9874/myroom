package com.example.myroom.domain.model3D.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "치수 추출용 이미지 분석 요청 메시지")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDimensionsImageRequestMessage {

    @Schema(description = "3D 모델 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
    @JsonProperty("model3d_id")
    private Long model3dId;

    @Schema(description = "요청 회원 ID", requiredMode = RequiredMode.REQUIRED, example = "10")
    @JsonProperty("member_id")
    private Long memberId;

    @Schema(description = "분석할 이미지 URL", requiredMode = RequiredMode.REQUIRED, example = "https://asa-room.s3.amazonaws.com/model3d/dimensions/images/a.png")
    @JsonProperty("image_url")
    private String imageUrl;

    @Schema(description = "요청 시각 (ms)", requiredMode = RequiredMode.REQUIRED, example = "1700000000000")
    private Long timestamp;
}
