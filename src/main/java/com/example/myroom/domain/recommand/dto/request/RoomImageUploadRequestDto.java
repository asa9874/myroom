package com.example.myroom.domain.recommand.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@JsonNaming(SnakeCaseStrategy.class)
public record RoomImageUploadRequestDto(
        @Schema(description = "방 사진 URL", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "이미지 URL은 비어 있을 수 없습니다.")
        String imageUrl
) {
    
}
