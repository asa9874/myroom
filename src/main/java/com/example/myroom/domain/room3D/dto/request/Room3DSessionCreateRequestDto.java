package com.example.myroom.domain.room3D.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "3D 룸 세션 생성 요청 DTO")
public record Room3DSessionCreateRequestDto(

    @Schema(description = "3D 룸 에셋 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "에셋 ID는 필수입니다.")
    Long assetId,

    @Schema(description = "세션 이름", requiredMode = Schema.RequiredMode.REQUIRED, example = "나만의 거실 꾸미기")
    @NotBlank(message = "세션 이름은 필수입니다.")
    String sessionName,

    @Schema(description = "세션 설명", example = "따뜻한 느낌의 거실을 만들어봤습니다.")
    String sessionDescription,

    @Schema(description = "공유 여부", example = "false")
    Boolean isShared,

    @Schema(description = "사용된 3D 모델 ID 목록", example = "[1, 2, 3]")
    List<Long> model3dIds
) {
}
