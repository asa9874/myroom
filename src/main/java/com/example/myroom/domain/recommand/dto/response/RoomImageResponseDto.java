package com.example.myroom.domain.recommand.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.recommand.model.RoomImage;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record RoomImageResponseDto(
        @Schema(description = "이미지 ID", requiredMode = RequiredMode.REQUIRED)
        Long id,
        
        @Schema(description = "회원 ID", requiredMode = RequiredMode.REQUIRED)
        Long memberId,
        
        @Schema(description = "이미지 URL", requiredMode = RequiredMode.REQUIRED)
        String imageUrl,
        
        @Schema(description = "생성 일시", requiredMode = RequiredMode.REQUIRED)
        LocalDateTime createdAt
) {
    public static RoomImageResponseDto from(RoomImage roomImage) {
        return new RoomImageResponseDto(
                roomImage.getId(),
                roomImage.getMemberId(),
                roomImage.getImageUrl(),
                roomImage.getCreatedAt()
        );
    }
    
}
