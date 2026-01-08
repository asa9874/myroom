package com.example.myroom.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

    public static final String MODEL3D_EXCHANGE = "model3d.exchange";
    public static final String MODEL3D_QUEUE = "model3d.upload.queue";
    public static final String MODEL3D_ROUTING_KEY = "model3d.upload";

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public TopicExchange model3dExchange() {
        return new TopicExchange(MODEL3D_EXCHANGE, true, false);
    }

    @Bean
    public Queue model3dQueue() {
        return new Queue(MODEL3D_QUEUE, true);
    }

    @Bean
    public Binding model3dBinding(Queue model3dQueue, TopicExchange model3dExchange) {
        return BindingBuilder.bind(model3dQueue)
                .to(model3dExchange)
                .with(MODEL3D_ROUTING_KEY);
    }
}