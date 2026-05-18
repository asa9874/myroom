package com.example.myroom.fake.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "가짜 방 생성 요청")
@JsonNaming(SnakeCaseStrategy.class)
public record FakeRoomCreateRequestDto(
        @Schema(description = "방 이름", requiredMode = RequiredMode.REQUIRED, example = "안방")
        @NotEmpty(message = "방 이름은 필수입니다.")
        String roomName,
        @Schema(description = "방 설명", requiredMode = RequiredMode.NOT_REQUIRED, example = "동쪽 벽에 창문이 있는 방")
        String description
) {
}
