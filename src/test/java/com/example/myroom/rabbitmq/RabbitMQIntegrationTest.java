package com.example.myroom.rabbitmq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;

/**
 * RabbitMQ 통합 테스트
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672",
        "spring.rabbitmq.username=guest",
        "spring.rabbitmq.password=guest"
})
@DisplayName("RabbitMQ 통합 테스트")
class RabbitMQIntegrationTest {

    @Autowired
    private RabbitMQTestProducer producer;

    @Autowired
    private RabbitMQTestConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer.clearReceivedMessages();
    }

    @Test
    @DisplayName("메시지 발송 및 수신 테스트")
    void testSendAndReceiveMessage() {
        // Given
        String testId = "test-1";
        String testContent = "Hello RabbitMQ!";

        // When
        producer.sendTestMessage(testId, testContent);

        // Then - 최대 5초 대기
        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertEquals(1, consumer.getMessageCount(), "메시지가 수신되어야 합니다");
                    RabbitMQTestMessage receivedMessage = consumer.getReceivedMessages().get(0);
                    assertEquals(testId, receivedMessage.getId());
                    assertEquals(testContent, receivedMessage.getContent());
                });
    }

    @Test
    @DisplayName("다중 메시지 발송 및 수신 테스트")
    void testSendMultipleMessages() {
        // Given
        int messageCount = 3;

        // When
        for (int i = 0; i < messageCount; i++) {
            producer.sendTestMessage("test-" + i, "message-" + i);
        }

        // Then
        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertEquals(messageCount, consumer.getMessageCount(), 
                            messageCount + "개의 메시지가 수신되어야 합니다");
                });
    }

    @Test
    @DisplayName("메시지 내용 검증 테스트")
    void testMessageContent() {
        // Given
        String testId = "test-content";
        String testContent = "Test Message Content";

        // When
        producer.sendTestMessage(testId, testContent);

        // Then
        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertFalse(consumer.getReceivedMessages().isEmpty());
                    RabbitMQTestMessage message = consumer.getReceivedMessages().get(0);
                    assertNotNull(message.getId());
                    assertNotNull(message.getContent());
                    assertNotNull(message.getTimestamp());
                    assertTrue(message.getTimestamp() > 0);
                });
    }
}
