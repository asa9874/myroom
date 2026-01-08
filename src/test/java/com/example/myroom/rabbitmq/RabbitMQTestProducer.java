package com.example.myroom.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 테스트 메시지 발송
 */
@Component
@RequiredArgsConstructor
public class RabbitMQTestProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 테스트 메시지 발송
     */
    public void sendTestMessage(String id, String content) {
        RabbitMQTestMessage message = RabbitMQTestMessage.builder()
                .id(id)
                .content(content)
                .timestamp(System.currentTimeMillis())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQTestConfig.TEST_EXCHANGE,
                RabbitMQTestConfig.TEST_ROUTING_KEY,
                message
        );

    }
}
