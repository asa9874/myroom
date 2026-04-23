package com.example.myroom.domain.room3D.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Room3D 수정 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record Room3DUpdateRequestDto(
        @Schema(
            description = "방 이름 (null이면 변경 없음)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "거실"
        )
        String roomName,

        @Schema(
            description = "방 설명 (null이면 변경 없음)",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "대형 창문이 있는 거실"
        )
        String description
) {
}
