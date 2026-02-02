package com.example.myroom.domain.socket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.example.myroom.domain.socket.dto.Model3DNotificationMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 컨트롤러
 * - 클라이언트로부터 메시지를 받아 처리하고 응답
 * 
 * ### 📡 WebSocket 연결 정보
 * - **연결 엔드포인트:** `/ws` (SockJS 지원)
 * - **프로토콜:** STOMP over WebSocket
 * - **인증:** JWT 토큰을 쿼리 파라미터로 전달 (`/ws?token={jwt_token}`)
 * 
 * ### 📋 사용 가능한 메시지 매핑
 * 1. `/app/test` → `/topic/test` (테스트 메시지 에코)
 * 2. `/app/subscribe` → `/topic/notifications/all` (구독 확인)  
 * 3. `/app/ping` → `/topic/pong` (연결 상태 확인)
 * 
 * ### 🔄 구독 토픽 목록
 * - `/topic/model3d/{userId}` - 개인 3D 모델 생성 알림
 * - `/topic/model3d/all` - 전체 3D 모델 생성 브로드캐스트
 * - `/topic/recommand/{userId}` - 개인 가구 추천 결과
 * - `/topic/test` - 테스트 메시지 응답
 * - `/topic/pong` - Ping/Pong 응답
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    /**
     * 클라이언트가 /app/test 로 메시지를 보내면 처리
     * 처리 후 /topic/test 를 구독한 모든 클라이언트에게 브로드캐스트
     * 
     * ### 📤 요청 메시지 예시
     * ```json
     * {
     *   "notificationType": "TEST_MESSAGE",
     *   "memberId": 1,
     *   "message": "테스트 메시지입니다.",
     *   "timestamp": 1705312300000
     * }
     * ```
     * 
     * ### 📥 응답 메시지 예시 (→ /topic/test)
     * ```json
     * {
     *   "notificationType": "TEST_MESSAGE",
     *   "memberId": 1,
     *   "originalImageUrl": null,
     *   "model3dUrl": null,
     *   "thumbnailUrl": null,
     *   "status": null,
     *   "message": "테스트 메시지입니다.",
     *   "processingTimeSeconds": null,
     *   "timestamp": 1705312300123
     * }
     * ```
     * 
     * @param message 클라이언트가 보낸 메시지
     * @param headerAccessor WebSocket 세션 정보
     * @return 모든 구독자에게 전송될 메시지
     */
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public Model3DNotificationMessage handleTestMessage(
            @Payload Model3DNotificationMessage message,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.info("📩 WebSocket 테스트 메시지 수신: {}", message);
        log.info("🖼️ 수신된 이미지 URL: originalImageUrl={}", message.getOriginalImageUrl());
        log.info("🎨 수신된 3D 모델 URL: model3dUrl={}", message.getModel3dUrl());
        
        // WebSocket 세션에서 사용자 정보 가져오기 (JWT에서 추출된 정보)
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String email = (String) headerAccessor.getSessionAttributes().get("email");
        
        log.info("사용자 정보: userId={}, email={}", userId, email);
        log.info("메시지 타입: {}, 상태: {}", message.getNotificationType(), message.getStatus());
        
        // 메시지에 타임스탬프 추가
        message.setTimestamp(System.currentTimeMillis());
        
        return message;
    }

    /**
     * 클라이언트가 /app/subscribe 로 구독 요청
     * JWT 토큰에서 추출한 사용자 정보로 구독 확인 메시지를 전송
     * 
     * ### 📥 응답 메시지 예시 (→ /topic/notifications/all)
     * ```json
     * {
     *   "notificationType": "SUBSCRIPTION_CONFIRMED",
     *   "memberId": 1,
     *   "originalImageUrl": null,
     *   "model3dUrl": null,
     *   "thumbnailUrl": null,
     *   "status": null,
     *   "message": "WebSocket 연결이 성공적으로 설정되었습니다.",
     *   "processingTimeSeconds": null,
     *   "timestamp": 1705312300000
     * }
     * ```
     * 
     * @param headerAccessor WebSocket 세션 정보
     * @return 구독 확인 메시지
     */
    @MessageMapping("/subscribe")
    @SendTo("/topic/notifications/all")
    public Model3DNotificationMessage handleSubscription(SimpMessageHeaderAccessor headerAccessor) {
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        
        log.info("🔔 새로운 구독자: userId={}", userId);
        
        return Model3DNotificationMessage.builder()
                .notificationType("SUBSCRIPTION_CONFIRMED")
                .memberId(userId)
                .message("WebSocket 연결이 성공적으로 설정되었습니다.")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Ping/Pong - 연결 상태 확인용
     * 클라이언트가 /app/ping 으로 메시지를 보내면 즉시 응답
     * 
     * ### 📤 요청
     * - 메시지 내용: 빈 문자열 또는 임의 텍스트
     * - 전송 경로: `/app/ping`
     * 
     * ### 📥 응답 (→ /topic/pong)
     * ```
     * "pong"
     * ```
     * 
     * **사용 목적:** WebSocket 연결 상태 확인 및 Keep-Alive
     */
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String handlePing() {
        log.debug("🏓 Ping 수신");
        return "pong";
    }
}
