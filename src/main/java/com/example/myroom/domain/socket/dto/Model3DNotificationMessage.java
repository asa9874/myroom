package com.example.myroom.domain.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket으로 전송할 3D 모델 알림 메시지
 * - RabbitMQ에서 받은 3D 모델 생성 완료 정보를 클라이언트에게 실시간으로 전달
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model3DNotificationMessage {
    
    /**
     * 알림 타입 (MODEL_GENERATION_SUCCESS, MODEL_GENERATION_FAILED 등)
     */
    private String notificationType;
    
    /**
     * 대상 회원 ID
     */
    private Long memberId;
    
    /**
     * 원본 이미지 URL
     */
    private String originalImageUrl;
    
    /**
     * 생성된 3D 모델 URL (성공 시)
     */
    private String model3dUrl;
    
    /**
     * 썸네일 URL (선택사항)
     */
    private String thumbnailUrl;
    
    /**
     * 생성 상태 (SUCCESS, FAILED)
     */
    private String status;
    
    /**
     * 메시지 내용
     */
    private String message;
    
    /**
     * 처리 시간 (초)
     */
    private Integer processingTimeSeconds;
    
    /**
     * 타임스탬프 (밀리초)
     */
    private Long timestamp;
}
