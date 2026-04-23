package com.example.myroom.domain.room3D.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Room3D 생성 요청 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record Room3DCreateRequestDto(
        @Schema(
            description = "방 이름",
            requiredMode = RequiredMode.REQUIRED,
            example = "안방"
        )
        @NotEmpty(message = "방 이름은 비어 있을 수 없습니다.")
        String roomName,

        @Schema(
            description = "방 설명",
            requiredMode = RequiredMode.NOT_REQUIRED,
            example = "붙박이장이 있는 안방"
        )
        String description
) {
}
