package com.example.myroom.domain.recommand.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.myroom.domain.recommand.dto.message.RecommandRequestMessage;
import com.example.myroom.global.config.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 추천 요청 메시지 발송
 * - 이미지 URL을 메시지 큐에 발송합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommandProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    // MQLog 파일용 로거
    private static final Logger mqLogger = LoggerFactory.getLogger("com.example.myroom.rabbitmq.mq");

    /**
     * 추천 요청 메시지 발송 (기본값 사용)
     * - 기본값: category='chair', topK=5
     * 
     * @param imageUrl 분석할 이미지 URL
     * @param memberId 요청한 회원 ID
     */
    public void sendRecommandRequestMessage(String imageUrl, Long memberId) {
        sendRecommandRequestMessage(imageUrl, memberId, "chair", 5);
    }

    /**
     * 추천 요청 메시지 발송 (모든 옵션 지정)
     * 
     * @param imageUrl 분석할 이미지 URL
     * @param memberId 요청한 회원 ID
     * @param category 추천할 가구 카테고리 (예: 'chair', 'table', 'lamp', 'sofa')
     * @param topK 반환할 추천 결과 개수
     */
    public void sendRecommandRequestMessage(String imageUrl, Long memberId, String category, Integer topK) {
        RecommandRequestMessage message = RecommandRequestMessage.builder()
                .imageUrl(imageUrl)
                .memberId(memberId)
                .category(category)
                .topK(topK)
                .timestamp(System.currentTimeMillis())
                .build();

        log.info("추천 요청 메시지 발송: imageUrl={}, memberId={}, category={}, topK={}", 
                imageUrl, memberId, category, topK);

        try {
            // MQLog 파일에 JSON 형식으로 저장
            String jsonMessage = objectMapper.writeValueAsString(message);
            mqLogger.info("=== [PRODUCER] RECOMMAND REQUEST ===");
            mqLogger.info("Exchange: {}", RabbitConfig.RECOMMAND_EXCHANGE);
            mqLogger.info("RoutingKey: {}", RabbitConfig.RECOMMAND_ROUTING_KEY);
            mqLogger.info("Message: {}", jsonMessage);
            mqLogger.info("=====================================\n");
        } catch (Exception e) {
            log.warn("Failed to log message to MQLog: {}", e.getMessage());
        }

        rabbitTemplate.convertAndSend(
                RabbitConfig.RECOMMAND_EXCHANGE,      // 어느 교환기(우체국)로 보낼지
                RabbitConfig.RECOMMAND_ROUTING_KEY,   // 어떤 주소(라우팅 키)로 보낼지
                message                                // 보낼 내용(메시지 객체)
        );
    }
}
