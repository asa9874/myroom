package com.example.myroom.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.myroom.global.jwt.JwtHandshakeInterceptor;

import lombok.NonNull;

@Configuration
@EnableWebSocketMessageBroker // 웹소켓 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:8080", "http://127.0.0.1:3000")
                .addInterceptors(jwtHandshakeInterceptor)
                .withSockJS();
    }
    
}