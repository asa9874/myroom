package com.example.myroom.domain.room3D.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.room3D.model.Room3D;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Room3D 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record Room3DResponseDto(
        @Schema(description = "Room3D ID", requiredMode = RequiredMode.REQUIRED, example = "1")
        Long id,

        @Schema(description = "회원 ID", requiredMode = RequiredMode.REQUIRED, example = "10")
        Long memberId,

        @Schema(description = "방 이름", requiredMode = RequiredMode.REQUIRED, example = "안방")
        String roomName,

        @Schema(description = "방 설명", requiredMode = RequiredMode.NOT_REQUIRED, example = "붙박이장이 있는 안방")
        String description,

        @Schema(description = "도면 이미지 URL", requiredMode = RequiredMode.REQUIRED, example = "https://asa-room.s3.amazonaws.com/room3d/images/a.png")
        String drawingImageUrl,

        @Schema(description = "도면 XML 파일 URL", requiredMode = RequiredMode.NOT_REQUIRED, example = "https://asa-room.s3.amazonaws.com/room3d/xml/a.xml")
        String drawingXmlUrl,

        @Schema(description = "AI 처리 성공 여부 (초기 null)", requiredMode = RequiredMode.NOT_REQUIRED, example = "true")
        Boolean success,

        @Schema(description = "생성 시각", requiredMode = RequiredMode.REQUIRED, example = "2026-04-23T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "수정 시각", requiredMode = RequiredMode.REQUIRED, example = "2026-04-23T12:10:00")
        LocalDateTime updatedAt
) {
    public static Room3DResponseDto from(Room3D room3D) {
        return new Room3DResponseDto(
                room3D.getId(),
                room3D.getMember().getId(),
                room3D.getRoomName(),
                room3D.getDescription(),
                room3D.getDrawingImageUrl(),
                room3D.getDrawingXmlUrl(),
                room3D.getSuccess(),
                room3D.getCreatedAt(),
                room3D.getUpdatedAt()
        );
    }
}
