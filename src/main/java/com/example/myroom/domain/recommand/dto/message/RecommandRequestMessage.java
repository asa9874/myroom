package com.example.myroom.domain.recommand.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추천 요청 메시지
 * - 이미지 URL을 포함하여 메시지 큐에 발송됩니다.
 */
@Schema(description = "AI 가구 추천 요청 메시지 - RabbitMQ를 통해 AI 서버로 전송되는 메시지 형식")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommandRequestMessage {
    
    /**
     * 요청한 회원 ID
     */
    @Schema(
        description = "추천을 요청한 회원의 고유 ID", 
        requiredMode = RequiredMode.REQUIRED,
        example = "1"
    )
    private Long memberId;
    
    /**
     * 분석할 이미지 URL
     */
    @Schema(
        description = "분석할 방 이미지의 S3 URL", 
        requiredMode = RequiredMode.REQUIRED,
        example = "https://s3.amazonaws.com/myroom-bucket/recommand/room_image_123.jpg"
    )
    private String imageUrl;
    
    /**
     * 추천할 가구 카테고리 (선택사항, 기본값: 'chair')
     * - 예: 'chair', 'table', 'lamp', 'sofa' 등
     */
    @Schema(
        description = "추천받을 가구 카테고리 (chair, table, sofa, bed, lamp, desk, shelf 등)", 
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "chair",
        defaultValue = "chair",
        allowableValues = {"chair", "table", "sofa", "bed", "lamp", "desk", "shelf"}
    )
    private String category;
    
    /**
     * 반환할 추천 결과 개수 (선택사항, 기본값: 5)
     * - 최대 개수는 별도로 제한할 수 있음
     */
    @Schema(
        description = "반환받을 추천 결과 개수 (1~100)", 
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "5",
        defaultValue = "5",
        minimum = "1",
        maximum = "100"
    )
    private Integer topK;
    
    /**
     * 요청 시각 (Unix timestamp, milliseconds)
     */
    @Schema(
        description = "요청 시각 (Unix timestamp, milliseconds)", 
        requiredMode = RequiredMode.REQUIRED,
        example = "1705312200000"
    )
    private Long timestamp;
}
