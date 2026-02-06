package com.example.myroom.domain.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "테스트 데이터 생성 결과 DTO")
public record TestDataResultDto(
        @Schema(description = "생성된 데이터 개수", example = "5")
        int createdCount,
        
        @Schema(description = "결과 메시지", example = "5개의 테스트 회원이 생성되었습니다.")
        String message,
        
        @Schema(description = "생성된 ID 목록", example = "[1, 2, 3, 4, 5]")
        java.util.List<Long> createdIds
) {
    public static TestDataResultDto of(int count, String message, java.util.List<Long> ids) {
        return new TestDataResultDto(count, message, ids);
    }
}