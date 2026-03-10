package com.example.myroom.domain.model3D.dto.request;

import com.example.myroom.domain.model3D.model.FurnitureCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "3D 모델 수정 요청 DTO v3 (furnitureType 포함, link 제거)")
@JsonNaming(SnakeCaseStrategy.class)
public record Model3DUpdateRequestV3Dto(
        @Schema(
            description = "변경할 3D 모델 이름 (null이면 변경 없음)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "모던 소파",
            maxLength = 100
        )
        String name,

        @Schema(
            description = "변경할 공유 여부 (null이면 변경 없음)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "true"
        )
        @JsonProperty("is_shared")
        Boolean isShared,

        @Schema(
            description = "변경할 모델 설명 (null이면 변경 없음)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "편안한 3인용 소파입니다. 거실용으로 적합합니다.",
            maxLength = 1000
        )
        String description,

        @Schema(
            description = "변경할 가구 종류 (null이면 변경 없음)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "chair",
            allowableValues = {"shelf", "sofa", "storage", "chair", "lighting", "desk", "bed", "table", "others"}
        )
        @JsonProperty("furniture_type")
        FurnitureCategory furnitureType,

        @Schema(
            description = "변경할 쇼핑몰 페이지 링크 (null이면 변경 없음)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "https://shop.example.com/products/sofa-456",
            maxLength = 2083
        )
        String shopPageLink
) {
}
