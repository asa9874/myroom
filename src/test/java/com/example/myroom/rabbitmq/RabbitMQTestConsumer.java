package com.example.myroom.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * RabbitMQ 테스트 메시지 수신
 */
@Slf4j
@Component
public class RabbitMQTestConsumer {

    private final List<RabbitMQTestMessage> receivedMessages = new ArrayList<>();

    @RabbitListener(queues = RabbitMQTestConfig.TEST_QUEUE)
    public void consumeTestMessage(RabbitMQTestMessage message) {
        log.info("메시지 수신: id={}, content={}, timestamp={}", 
                message.getId(), message.getContent(), message.getTimestamp());
        receivedMessages.add(message);
    }

    /**
     * 수신된 메시지 조회
     */
    public List<RabbitMQTestMessage> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }

    /**
     * 수신된 메시지 초기화
     */
    public void clearReceivedMessages() {
        receivedMessages.clear();
    }

    /**
     * 수신된 메시지 개수
     */
    public int getMessageCount() {
        return receivedMessages.size();
    }
}
