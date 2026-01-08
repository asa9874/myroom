package com.example.myroom.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RabbitMQ 테스트용 메시지 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMQTestMessage {
    private String id;
    private String content;
    private long timestamp;
}
