package com.example.myroom.domain.room3D.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Room3D AI 요청 메시지")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room3DRequestMessage {

    @Schema(description = "Room3D ID", requiredMode = RequiredMode.REQUIRED, example = "1")
    private Long room3dId;

    @Schema(description = "요청 회원 ID", requiredMode = RequiredMode.REQUIRED, example = "10")
    private Long memberId;

    @Schema(description = "도면 이미지 URL", requiredMode = RequiredMode.REQUIRED, example = "https://asa-room.s3.amazonaws.com/room3d/images/a.png")
    private String drawingImageUrl;

    @Schema(description = "방 이름", requiredMode = RequiredMode.REQUIRED, example = "안방")
    private String roomName;

    @Schema(description = "방 설명", requiredMode = RequiredMode.NOT_REQUIRED, example = "붙박이장이 있는 안방")
    private String description;

    @Schema(description = "요청 시각 (ms)", requiredMode = RequiredMode.REQUIRED, example = "1700000000000")
    private Long timestamp;
}
