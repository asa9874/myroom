package com.example.myroom.domain.socket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket으로 전송할 3D 모델 알림 메시지
 * - RabbitMQ에서 받은 3D 모델 생성 완료 정보를 클라이언트에게 실시간으로 전달
 */
@Schema(description = "3D 모델 생성 알림 메시지 - WebSocket을 통해 클라이언트로 전송되는 모델 생성 결과")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model3DNotificationMessage {
    
    /**
     * 알림 타입 (MODEL_GENERATION_SUCCESS, MODEL_GENERATION_FAILED 등)
     */
    @Schema(
        description = "알림 유형", 
        requiredMode = RequiredMode.REQUIRED,
        example = "MODEL_GENERATION_SUCCESS",
        allowableValues = {"MODEL_GENERATION_SUCCESS", "MODEL_GENERATION_FAILED", "MODEL_GENERATION_PROGRESS"}
    )
    private String notificationType;
    
    /**
     * 대상 회원 ID
     */
    @Schema(
        description = "알림을 받을 회원의 고유 ID", 
        requiredMode = RequiredMode.REQUIRED,
        example = "1"
    )
    private Long memberId;
    
    /**
     * 원본 이미지 URL
     */
    @Schema(
        description = "3D 모델 생성에 사용된 원본 이미지 URL", 
        requiredMode = RequiredMode.REQUIRED,
        example = "https://s3.amazonaws.com/myroom-bucket/images/chair_original.jpg"
    )
    private String originalImageUrl;
    
    /**
     * 생성된 3D 모델 URL (성공 시)
     */
    @Schema(
        description = "생성된 3D 모델 파일의 URL (성공 시에만 제공)", 
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "https://s3.amazonaws.com/myroom-bucket/models/chair.glb"
    )
    private String model3dUrl;
    
    /**
     * 썸네일 URL (선택사항)
     */
    @Schema(
        description = "생성된 3D 모델의 썸네일 이미지 URL", 
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "https://s3.amazonaws.com/myroom-bucket/thumbnails/chair_thumb.png"
    )
    private String thumbnailUrl;
    
    /**
     * 생성 상태 (SUCCESS, FAILED)
     */
    @Schema(
        description = "3D 모델 생성 상태", 
        requiredMode = RequiredMode.REQUIRED,
        example = "SUCCESS",
        allowableValues = {"SUCCESS", "FAILED", "PROCESSING"}
    )
    private String status;
    
    /**
     * 메시지 내용
     */
    @Schema(
        description = "사용자에게 표시할 상태 메시지", 
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "3D 모델이 성공적으로 생성되었습니다."
    )
    private String message;
    
    /**
     * 처리 시간 (초)
     */
    @Schema(
        description = "3D 모델 생성에 소요된 시간 (초)", 
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "45"
    )
    private Integer processingTimeSeconds;
    
    /**
     * 타임스탬프 (밀리초)
     */
    @Schema(
        description = "알림 생성 시각 (Unix timestamp, milliseconds)", 
        requiredMode = RequiredMode.REQUIRED,
        example = "1705312300000"
    )
    private Long timestamp;
}
