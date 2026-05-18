package com.example.myroom.fake.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.fake.model.FakeRoom;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "가짜 방 응답")
@JsonNaming(SnakeCaseStrategy.class)
public record FakeRoomResponseDto(
        @Schema(description = "가짜 방 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
        Long id,
        @Schema(description = "방 이름", requiredMode = RequiredMode.REQUIRED, example = "안방")
        String roomName,
        @Schema(description = "방 설명", requiredMode = RequiredMode.NOT_REQUIRED, example = "동쪽 벽에 창문이 있는 방")
        String description,
        @Schema(
            description = "XML 파일 URL",
            requiredMode = RequiredMode.REQUIRED,
            example = "https://example-bucket.s3.amazonaws.com/fake-room/xml/a.xml"
        )
        String xmlFileUrl,
        @Schema(description = "생성 시각", requiredMode = RequiredMode.REQUIRED, example = "2026-05-18T12:00:00")
        LocalDateTime createdAt,
        @Schema(description = "수정 시각", requiredMode = RequiredMode.REQUIRED, example = "2026-05-18T12:05:00")
        LocalDateTime updatedAt
) {
    public static FakeRoomResponseDto from(FakeRoom fakeRoom) {
        return new FakeRoomResponseDto(
                fakeRoom.getId(),
                fakeRoom.getRoomName(),
                fakeRoom.getDescription(),
                fakeRoom.getXmlFileUrl(),
                fakeRoom.getCreatedAt(),
                fakeRoom.getUpdatedAt()
        );
    }
}
