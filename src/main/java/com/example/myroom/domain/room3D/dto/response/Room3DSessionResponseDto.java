package com.example.myroom.domain.room3D.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.myroom.domain.room3D.model.Room3DSession;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "3D 룸 세션 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record Room3DSessionResponseDto(

    @Schema(description = "세션 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
    Long id,

    @Schema(description = "에셋 ID", requiredMode = RequiredMode.REQUIRED, example = "1")
    Long assetId,

    @Schema(description = "에셋 이름", example = "거실룸")
    String assetName,

    @Schema(description = "회원 ID", requiredMode = RequiredMode.REQUIRED, example = "10")
    Long memberId,

    @Schema(description = "회원 이름", example = "홍길동")
    String memberName,

    @Schema(description = "XML 파일 URL", example = "https://s3.amazonaws.com/bucket/sessions/session_1.xml")
    String xmlFileUrl,

    @Schema(description = "세션 이름", requiredMode = RequiredMode.REQUIRED, example = "나만의 거실 꾸미기")
    String sessionName,

    @Schema(description = "세션 설명", example = "따뜻한 느낌의 거실을 만들어봤습니다.")
    String sessionDescription,

    @Schema(description = "공유 여부", example = "false")
    Boolean isShared,

    @Schema(description = "사용된 3D 모델 ID 목록", example = "[1, 2, 3]")
    List<Long> model3dIds,

    @Schema(description = "생성 날짜", example = "2026-03-10T10:30:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 날짜", example = "2026-03-10T15:45:00")
    LocalDateTime updatedAt
) {
    public static Room3DSessionResponseDto from(Room3DSession session) {
        return new Room3DSessionResponseDto(
                session.getId(),
                session.getAsset().getId(),
                session.getAsset().getName(),
                session.getMember().getId(),
                session.getMember().getName(),
                session.getXmlFileUrl(),
                session.getSessionName(),
                session.getSessionDescription(),
                session.getIsShared(),
                session.getModel3Ds().stream()
                        .map(model -> model.getId())
                        .toList(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
