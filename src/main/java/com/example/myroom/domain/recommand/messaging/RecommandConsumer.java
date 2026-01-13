package com.example.myroom.domain.recommand.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.myroom.domain.recommand.dto.message.RecommandResponseMessage;
import com.example.myroom.domain.recommand.service.RecommandService;
import com.example.myroom.domain.socket.service.WebSocketNotificationService;
import com.example.myroom.global.config.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 추천 결과 메시지 수신
 * - AI 추천 서버에서 보낸 분석 및 추천 결과 메시지를 소비(Consume)합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommandConsumer {

    private final RecommandService recommandService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final ObjectMapper objectMapper;
    
    // MQLog 파일용 로거
    private static final Logger mqLogger = LoggerFactory.getLogger("com.example.myroom.rabbitmq.mq");

    /**
     * 추천 결과 메시지 처리
     * 
     * @RabbitListener: 지정된 큐를 구독하여 메시지가 도착하면 자동으로 이 메서드를 실행합니다.
     * - queues: 구독할 큐 이름 지정
     * - Spring Boot가 자동으로 JSON 메시지를 RecommandResponseMessage 객체로 변환해줍니다.
     * 
     * @param response AI 추천 서버로부터 받은 응답 메시지
     */
    @RabbitListener(queues = RabbitConfig.RECOMMAND_RESPONSE_QUEUE)
    public void handleRecommandResponse(RecommandResponseMessage response) {
        log.info("========================================");
        log.info("추천 결과 메시지 수신");
        log.info("========================================");
        log.info("회원 ID: {}", response.getMemberId());
        log.info("처리 상태: {}", response.getStatus());
        
        if (response.getRoomAnalysis() != null) {
            log.info("방 스타일: {}", response.getRoomAnalysis().getStyle());
            log.info("방 색상: {}", response.getRoomAnalysis().getColor());
            log.info("감지된 가구 수: {}", response.getRoomAnalysis().getDetectedCount());
            log.info("감지된 가구: {}", response.getRoomAnalysis().getDetectedFurniture());
        }
        
        if (response.getRecommendation() != null) {
            log.info("추천 카테고리: {}", response.getRecommendation().getTargetCategory());
            log.info("추천 결과 수: {}", response.getRecommendation().getResultCount());
        }
        
        log.info("수신 시각: {}", response.getTimestamp());
        log.info("========================================");

        // MQLog 파일에 JSON 형식으로 저장
        try {
            String jsonMessage = objectMapper.writeValueAsString(response);
            mqLogger.info("=== [CONSUMER] RECOMMAND RESPONSE ===");
            mqLogger.info("Queue: {}", RabbitConfig.RECOMMAND_RESPONSE_QUEUE);
            mqLogger.info("Status: {}", response.getStatus());
            mqLogger.info("Message: {}", jsonMessage);
            mqLogger.info("=====================================\n");
        } catch (Exception e) {
            log.warn("Failed to log message to MQLog: {}", e.getMessage());
        }

        try {
            // 처리 상태에 따라 분기 처리
            if ("success".equalsIgnoreCase(response.getStatus())) {
                // 성공: 추천 정보를 DB에 저장
                log.info("✅ 추천 분석 성공 - DB 저장 시작");
                recommandService.saveRecommandResult(response);
                log.info("✅ DB 저장 완료");
                
                // WebSocket으로 클라이언트에게 실시간 알림 전송
                log.info("📤 WebSocket 알림 발송 시작 - 회원 {}에게 전송", response.getMemberId());
                webSocketNotificationService.sendRecommandNotification(response);
                log.info("✅ WebSocket 알림 발송 완료");
                
            } else if ("failed".equalsIgnoreCase(response.getStatus())) {
                // 실패: 에러 로그 기록 및 알림 처리
                log.error("❌ 추천 분석 실패 - 회원 ID: {}", response.getMemberId());
                recommandService.handleRecommandFailure(response);
                
                // 실패 시에도 WebSocket으로 알림 전송
                log.info("📤 WebSocket 실패 알림 발송 시작 - 회원 {}에게 전송", response.getMemberId());
                webSocketNotificationService.sendRecommandNotification(response);
                log.info("✅ WebSocket 실패 알림 발송 완료");
                
            } else {
                // 기타 상태 (PROCESSING 등)
                log.warn("⚠️ 알 수 없는 상태: {}", response.getStatus());
            }
            
        } catch (Exception e) {
            // 메시지 처리 중 에러 발생 시 로그 기록
            log.error("❌ 추천 결과 메시지 처리 중 오류 발생", e);
            log.error("에러 메시지: {}", e.getMessage());
            log.error("응답 데이터: {}", response);
            
            // 실제 운영 환경에서는:
            // 1. Dead Letter Queue로 메시지 이동
            // 2. 알림 서비스를 통해 관리자에게 알림
            // 3. 재시도 로직 구현
        }
    }
}
