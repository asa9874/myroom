package com.example.myroom.fake.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "가짜 방 수정 요청")
@JsonNaming(SnakeCaseStrategy.class)
public record FakeRoomUpdateRequestDto(
        @Schema(description = "방 이름", requiredMode = RequiredMode.NOT_REQUIRED, example = "거실")
        String roomName,
        @Schema(description = "방 설명", requiredMode = RequiredMode.NOT_REQUIRED, example = "창문이 큰 거실")
        String description
) {
}
