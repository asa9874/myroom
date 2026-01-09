package com.example.myroom.domain.model3D.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.myroom.domain.model3D.dto.message.Model3DUploadMessage;
import com.example.myroom.global.config.RabbitConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 3D 모델 메시지 발송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Model3DProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 3D 모델 업로드 메시지 발송
     */
    public void sendModel3DUploadMessage(String imageUrl, Long memberId) {
        Model3DUploadMessage message = Model3DUploadMessage.builder()
                .imageUrl(imageUrl)
                .memberId(memberId)
                .timestamp(System.currentTimeMillis())
                .build();

        log.info("3D 모델 업로드 메시지 발송: imageUrl={}, memberId={}", imageUrl, memberId);

        rabbitTemplate.convertAndSend(
                RabbitConfig.MODEL3D_EXCHANGE, // 어느 교환기(우체국)로 보낼지
                RabbitConfig.MODEL3D_ROUTING_KEY, // 어떤 주소(라우팅 키)로 보낼지
                message // 보낼 내용(메시지 객체)
        );
    }
}
