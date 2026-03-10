package com.example.myroom.domain.room3D.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "3D 룸 세션 정보 수정 요청 DTO")
public record Room3DSessionUpdateInfoRequestDto(

    @Schema(description = "세션 이름", example = "수정된 거실 꾸미기")
    String sessionName,

    @Schema(description = "세션 설명", example = "색감을 바꿔봤습니다.")
    String sessionDescription,

    @Schema(description = "공유 여부", example = "true")
    Boolean isShared,

    @Schema(description = "사용된 3D 모델 ID 목록", example = "[1, 3, 5]")
    List<Long> model3dIds
) {
}
