package com.example.myroom.domain.room3D.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.myroom.domain.room3D.dto.message.Room3DResponseMessage;
import com.example.myroom.domain.room3D.service.Room3DService;
import com.example.myroom.global.config.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Room3DConsumer {

    private final Room3DService room3DService;
    private final ObjectMapper objectMapper;

    private static final Logger mqLogger = LoggerFactory.getLogger("com.example.myroom.rabbitmq.mq");

    @RabbitListener(queues = RabbitConfig.ROOM3D_RESPONSE_QUEUE)
    public void handleRoom3DResponse(Room3DResponseMessage response) {
        log.info("Room3D 응답 메시지 수신: room3dId={}, memberId={}, status={}",
                response.getRoom3dId(), response.getMemberId(), response.getStatus());

        try {
            String jsonMessage = objectMapper.writeValueAsString(response);
            mqLogger.info("=== [CONSUMER] ROOM3D RESPONSE ===");
            mqLogger.info("Queue: {}", RabbitConfig.ROOM3D_RESPONSE_QUEUE);
            mqLogger.info("Status: {}", response.getStatus());
            mqLogger.info("Message: {}", jsonMessage);
            mqLogger.info("==================================\n");
        } catch (Exception e) {
            log.warn("Room3D MQ 로그 기록 실패: {}", e.getMessage());
        }

        try {
            room3DService.handleRoom3DResponse(response);
            log.info("Room3D 응답 처리 완료: room3dId={}", response.getRoom3dId());
        } catch (Exception e) {
            log.error("Room3D 응답 처리 실패: room3dId={}, error={}",
                    response.getRoom3dId(), e.getMessage(), e);
        }
    }
}
