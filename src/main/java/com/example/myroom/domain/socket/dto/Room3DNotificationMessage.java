package com.example.myroom.domain.socket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Room3D generation notification message delivered over WebSocket.
 */
@Schema(description = "Room3D generation notification message delivered over WebSocket")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room3DNotificationMessage {

    @Schema(
        description = "Notification type",
        requiredMode = RequiredMode.REQUIRED,
        example = "ROOM3D_GENERATION_SUCCESS",
        allowableValues = {"ROOM3D_GENERATION_SUCCESS", "ROOM3D_GENERATION_FAILED", "ROOM3D_GENERATION_PROGRESS"}
    )
    private String notificationType;

    @Schema(
        description = "Target member ID",
        requiredMode = RequiredMode.REQUIRED,
        example = "10"
    )
    private Long memberId;

    @Schema(
        description = "Room3D ID",
        requiredMode = RequiredMode.REQUIRED,
        example = "1"
    )
    private Long room3dId;

    @Schema(
        description = "Original drawing image URL",
        requiredMode = RequiredMode.REQUIRED,
        example = "https://asa-room.s3.amazonaws.com/room3d/images/a.png"
    )
    private String drawingImageUrl;

    @Schema(
        description = "Generated XML file URL",
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "https://asa-room.s3.amazonaws.com/room3d/xml/a.xml"
    )
    private String xmlFileUrl;

    @Schema(
        description = "Generation status",
        requiredMode = RequiredMode.REQUIRED,
        example = "SUCCESS",
        allowableValues = {"SUCCESS", "FAILED", "PROCESSING"}
    )
    private String status;

    @Schema(
        description = "Status message",
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "Room3D generation completed."
    )
    private String message;

    @Schema(
        description = "Notification timestamp (milliseconds)",
        requiredMode = RequiredMode.REQUIRED,
        example = "1705312300000"
    )
    private Long timestamp;
}
