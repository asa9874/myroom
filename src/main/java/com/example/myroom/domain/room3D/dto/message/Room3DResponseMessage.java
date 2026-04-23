package com.example.myroom.domain.room3D.dto.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Room3D AI 응답 메시지")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room3DResponseMessage {

    @Schema(description = "Room3D ID", requiredMode = RequiredMode.REQUIRED, example = "1")
    @JsonProperty("room3d_id")
    @JsonAlias({"room3dId", "room_id", "roomId"})
    private Long room3dId;

    @Schema(description = "요청 회원 ID", requiredMode = RequiredMode.REQUIRED, example = "10")
    @JsonProperty("member_id")
    @JsonAlias("memberId")
    private Long memberId;

    @Schema(description = "처리 상태", requiredMode = RequiredMode.REQUIRED, example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED"})
    private String status;

    @Schema(description = "생성된 XML URL", requiredMode = RequiredMode.NOT_REQUIRED, example = "https://asa-room.s3.amazonaws.com/room3d/xml/a.xml")
    @JsonProperty("xml_file_url")
    @JsonAlias({"xmlFileUrl", "drawingXmlUrl"})
    private String xmlFileUrl;

    @Schema(description = "상태 메시지", requiredMode = RequiredMode.NOT_REQUIRED, example = "completed")
    private String message;

    @Schema(description = "응답 시각 (ms)", requiredMode = RequiredMode.REQUIRED, example = "1700000000000")
    private Long timestamp;
}
