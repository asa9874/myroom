package com.example.myroom.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 테스트 설정
 */
@Configuration
public class RabbitMQTestConfig {

    public static final String TEST_EXCHANGE = "test.exchange";
    public static final String TEST_QUEUE = "test.queue";
    public static final String TEST_ROUTING_KEY = "test.routing.key";

    @Bean
    public TopicExchange testExchange() {
        return new TopicExchange(TEST_EXCHANGE, true, false);
    }

    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE, true);
    }

    @Bean
    public Binding testBinding(Queue testQueue, TopicExchange testExchange) {
        return BindingBuilder.bind(testQueue)
                .to(testExchange)
                .with(TEST_ROUTING_KEY);
    }
}
