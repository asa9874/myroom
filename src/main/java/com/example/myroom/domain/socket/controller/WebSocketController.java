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
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    /**
     * 클라이언트가 /app/test 로 메시지를 보내면 처리
     * 처리 후 /topic/test 를 구독한 모든 클라이언트에게 브로드캐스트
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
     */
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String handlePing() {
        log.debug("🏓 Ping 수신");
        return "pong";
    }
}
