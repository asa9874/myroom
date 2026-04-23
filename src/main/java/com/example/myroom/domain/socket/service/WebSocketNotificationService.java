package com.example.myroom.domain.socket.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.myroom.domain.model3D.dto.message.ModelDimensionsImageResponseMessage;
import com.example.myroom.domain.model3D.dto.message.Model3DGenerationResponse;
import com.example.myroom.domain.recommand.dto.message.RecommandResponseMessage;
import com.example.myroom.domain.socket.dto.Model3DNotificationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 알림 서비스
 * - STOMP 프로토콜을 사용하여 클라이언트에게 실시간 알림 전송
 * 
 * ### 📡 지원하는 알림 유형
 * 1. **3D 모델 생성 알림**
 *    - 개인 알림: `/topic/model3d/{memberId}`
 *    - 전체 브로드캐스트: `/topic/model3d/all`
 *    - 메시지 타입: Model3DNotificationMessage
 * 
 * 2. **가구 추천 결과 알림**
 *    - 개인 알림: `/topic/recommand/{memberId}`
 *    - 메시지 타입: RecommandResponseMessage
 * 
 * 3. **가구 치수 추출 결과 알림**
 *    - 개인 알림: `/topic/model-dimensions/{memberId}`
 *    - 메시지 타입: ModelDimensionsImageResponseMessage
 * 
 * 4. **커스텀 알림**
 *    - 개인 알림: `/topic/notifications/{memberId}`
 *    - 메시지 타입: Model3DNotificationMessage
 * 
 * ### 🔄 메시지 전송 방식
 * - **개인 알림**: 특정 사용자만 수신
 * - **브로드캐스트**: 연결된 모든 사용자가 수신
 * - **자동 변환**: RabbitMQ 응답을 WebSocket 메시지로 변환
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 특정 회원에게 3D 모델 생성 완료 알림 전송
     * 
     * @param response RabbitMQ로부터 받은 3D 모델 생성 응답
     */
    public void sendModel3DGenerationNotification(Model3DGenerationResponse response) {
        log.info("📤 WebSocket 알림 전송 시작: memberId={}, status={}", 
            response.getMemberId(), response.getStatus());
        log.info("🖼️ 이미지 정보: originalImageUrl={}", response.getOriginalImageUrl());
        log.info("🎨 3D 모델 URL: {}", response.getModel3dUrl());
        log.info("📸 썸네일 URL: {}", response.getThumbnailUrl());

        try {
            // RabbitMQ 응답을 WebSocket 메시지로 변환
            Model3DNotificationMessage notification = convertToNotification(response);
            
            log.info("📦 변환된 알림 메시지: {}", notification);
            
            // 특정 회원에게만 메시지 전송
            // 구독 경로: /topic/model3d/{memberId}
            String destination = "/topic/model3d/" + response.getMemberId();
            messagingTemplate.convertAndSend(destination, notification);
            
            log.info("✅ WebSocket 알림 전송 성공: destination={}", destination);
            log.info("✅ 전송된 메시지 타입: {}, 상태: {}", notification.getNotificationType(), notification.getStatus());
            
        } catch (Exception e) {
            log.error("❌ WebSocket 알림 전송 실패: memberId={}, error={}", 
                response.getMemberId(), e.getMessage(), e);
        }
    }

    /**
     * 모든 연결된 클라이언트에게 브로드캐스트
     * 
     * @param response RabbitMQ로부터 받은 3D 모델 생성 응답
     */
    public void broadcastModel3DGenerationNotification(Model3DGenerationResponse response) {
        log.info("📢 WebSocket 브로드캐스트 시작: status={}", response.getStatus());
        log.info("🖼️ 이미지 정보: originalImageUrl={}", response.getOriginalImageUrl());
        log.info("🎨 3D 모델 URL: {}", response.getModel3dUrl());

        try {
            Model3DNotificationMessage notification = convertToNotification(response);
            
            log.info("📦 브로드캐스트 메시지: {}", notification);
            
            // 모든 구독자에게 전송
            // 구독 경로: /topic/model3d/all
            messagingTemplate.convertAndSend("/topic/model3d/all", notification);
            
            log.info("✅ WebSocket 브로드캐스트 성공: destination=/topic/model3d/all");
            log.info("✅ 브로드캐스트 메시지 타입: {}, 상태: {}", notification.getNotificationType(), notification.getStatus());
            
        } catch (Exception e) {
            log.error("❌ WebSocket 브로드캐스트 실패: error={}", e.getMessage(), e);
        }
    }

    /**
     * RabbitMQ 응답을 WebSocket 알림 메시지로 변환
     */
    private Model3DNotificationMessage convertToNotification(Model3DGenerationResponse response) {
        // 알림 타입 결정
        String notificationType = "SUCCESS".equalsIgnoreCase(response.getStatus()) 
            ? "MODEL_GENERATION_SUCCESS" 
            : "MODEL_GENERATION_FAILED";
        
        return Model3DNotificationMessage.builder()
                .notificationType(notificationType)
                .memberId(response.getMemberId())
                .originalImageUrl(response.getOriginalImageUrl())
                .model3dUrl(response.getModel3dUrl())
                .thumbnailUrl(response.getThumbnailUrl())
                .status(response.getStatus())
                .message(response.getMessage())
                .processingTimeSeconds(response.getProcessingTimeSeconds())
                .timestamp(response.getTimestamp())
                .build();
    }

    /**
     * 사용자 정의 알림 전송
     * 
     * @param memberId 대상 회원 ID
     * @param message 알림 메시지
     */
    public void sendCustomNotification(Long memberId, String message) {
        log.info("📤 커스텀 알림 전송: memberId={}, message={}", memberId, message);

        try {
            Model3DNotificationMessage notification = Model3DNotificationMessage.builder()
                    .notificationType("CUSTOM_NOTIFICATION")
                    .memberId(memberId)
                    .message(message)
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            String destination = "/topic/notifications/" + memberId;
            messagingTemplate.convertAndSend(destination, notification);
            
            log.info("✅ 커스텀 알림 전송 성공");
            
        } catch (Exception e) {
            log.error("❌ 커스텀 알림 전송 실패: error={}", e.getMessage(), e);
        }
    }

    /**
     * 추천 결과 알림 전송
     * 
     * @param response RabbitMQ로부터 받은 추천 결과 응답
     */
    public void sendRecommandNotification(RecommandResponseMessage response) {
        log.info("📤 추천 결과 알림 전송 시작: memberId={}, status={}", 
            response.getMemberId(), response.getStatus());

        try {
            // 추천 결과를 알림 메시지로 변환하여 WebSocket으로 전송
            String destination = "/topic/recommand/" + response.getMemberId();
            messagingTemplate.convertAndSend(destination, response);
            
            log.info("✅ 추천 결과 알림 전송 성공: destination={}", destination);
            
        } catch (Exception e) {
            log.error("❌ 추천 결과 알림 전송 실패: memberId={}, error={}", 
                response.getMemberId(), e.getMessage(), e);
        }
    }

    /**
     * 가구 치수 추출 결과 알림 전송
     *
     * @param response RabbitMQ로부터 받은 치수 분석 결과 응답
     */
    public void sendModelDimensionsNotification(ModelDimensionsImageResponseMessage response) {
        log.info("📤 치수 분석 결과 알림 전송 시작: memberId={}, model3dId={}, status={}",
            response.getMemberId(), response.getModel3dId(), response.getStatus());

        if (response.getMemberId() == null) {
            log.error("❌ 치수 분석 결과 알림 전송 실패: memberId가 없습니다. response={}", response);
            return;
        }

        try {
            String destination = "/topic/model-dimensions/" + response.getMemberId();
            messagingTemplate.convertAndSend(destination, response);

            log.info("✅ 치수 분석 결과 알림 전송 성공: destination={}", destination);
        } catch (Exception e) {
            log.error("❌ 치수 분석 결과 알림 전송 실패: memberId={}, error={}",
                response.getMemberId(), e.getMessage(), e);
        }
    }
}
