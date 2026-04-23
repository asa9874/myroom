package com.example.myroom.domain.room3D.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.myroom.domain.room3D.dto.message.Room3DRequestMessage;
import com.example.myroom.global.config.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Room3DProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final Logger mqLogger = LoggerFactory.getLogger("com.example.myroom.rabbitmq.mq");

    public void sendRoom3DRequestMessage(
            Long room3dId,
            Long memberId,
            String drawingImageUrl,
            String roomName,
            String description) {
        Room3DRequestMessage message = Room3DRequestMessage.builder()
                .room3dId(room3dId)
                .memberId(memberId)
                .drawingImageUrl(drawingImageUrl)
                .roomName(roomName)
                .description(description)
                .timestamp(System.currentTimeMillis())
                .build();

        log.info("Room3D 요청 메시지 발송: room3dId={}, memberId={}", room3dId, memberId);

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            mqLogger.info("=== [PRODUCER] ROOM3D REQUEST ===");
            mqLogger.info("Exchange: {}", RabbitConfig.ROOM3D_EXCHANGE);
            mqLogger.info("RoutingKey: {}", RabbitConfig.ROOM3D_ROUTING_KEY);
            mqLogger.info("Message: {}", jsonMessage);
            mqLogger.info("=================================\n");
        } catch (Exception e) {
            log.warn("Room3D MQ 로그 기록 실패: {}", e.getMessage());
        }

        rabbitTemplate.convertAndSend(
                RabbitConfig.ROOM3D_EXCHANGE,
                RabbitConfig.ROOM3D_ROUTING_KEY,
                message);
    }
}
