package com.example.myroom.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

    // Exchange: 메시지를 적절한 큐로 배정하는 배선반 역할을 합니다.
    public static final String MODEL3D_EXCHANGE = "model3d.exchange";
    // Queue: 실제 메시지가 소비(Consume)되기 전까지 대기하는 저장소입니다.
    public static final String MODEL3D_QUEUE = "model3d.upload.queue";
    // Routing Key: Exchange가 메시지를 큐로 보낼 때 참조하는 '주소' 혹은 '태그'입니다.
    public static final String MODEL3D_ROUTING_KEY = "model3d.upload";

    // 3D 모델 생성 완료 응답을 받기 위한 설정
    // Queue: 3D 모델 생성 서버로부터 생성 완료 메시지를 받는 큐
    public static final String MODEL3D_RESPONSE_QUEUE = "model3d.response.queue";
    // Routing Key: 3D 모델 생성 완료 메시지용 라우팅 키
    public static final String MODEL3D_RESPONSE_ROUTING_KEY = "model3d.response";

    // ===== VectorDB 메타데이터 업데이트 설정 =====
    // Queue: VectorDB 메타데이터 업데이트 요청을 받는 큐
    public static final String MODEL3D_METADATA_UPDATE_QUEUE = "model3d.metadata.update.queue";
    // Routing Key: VectorDB 메타데이터 업데이트용 라우팅 키
    public static final String MODEL3D_METADATA_UPDATE_ROUTING_KEY = "model3d.metadata.update";

    // ===== VectorDB 삭제 설정 =====
    // Queue: VectorDB에서 3D 모델 삭제 요청을 받는 큐
    public static final String MODEL3D_DELETE_QUEUE = "model3d.delete.queue";
    // Routing Key: VectorDB 삭제용 라우팅 키
    public static final String MODEL3D_DELETE_ROUTING_KEY = "model3d.delete";

    // ===== 추천 기능 관련 설정 =====
    // Exchange: 추천 요청 메시지를 배정하는 교환기
    public static final String RECOMMAND_EXCHANGE = "recommand.exchange";
    // Queue: 추천 요청 메시지를 받는 큐
    public static final String RECOMMAND_QUEUE = "recommand.request.queue";
    // Routing Key: 추천 요청 메시지용 라우팅 키
    public static final String RECOMMAND_ROUTING_KEY = "recommand.request";
    
    // Queue: 추천 결과 응답 메시지를 받는 큐
    public static final String RECOMMAND_RESPONSE_QUEUE = "recommand.response.queue";
    // Routing Key: 추천 결과 응답 메시지용 라우팅 키
    public static final String RECOMMAND_RESPONSE_ROUTING_KEY = "recommand.response";

    /**
     * 메시지 컨버터 설정
     * - RabbitMQ는 기본적으로 byte 배열로 메시지를 전송합니다.
     * - Jackson2JsonMessageConverter를 사용하면 Java 객체(DTO)를 JSON으로 자동 변환하여 주고받을 수 있습니다.
     * - ObjectMapper는 Spring Boot가 관리하는 것을 주입받아 사용합니다.
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Topic Exchange 생성
     * - Topic 방식: 라우팅 키의 패턴을 보고 메시지를 배정 (예: model3d.# 등 확장 가능)
     * - durable(true): RabbitMQ 서버가 재시작되어도 Exchange 설정이 유지됩니다.
     * - autoDelete(false): 사용 중인 큐가 없어도 Exchange가 삭제되지 않습니다.
     */
    @Bean
    public TopicExchange model3dExchange() {
        return new TopicExchange(MODEL3D_EXCHANGE, true, false);
    }

    /**
     * Queue 생성
     * - durable(true): RabbitMQ 서버가 재시작되어도 큐와 그 안의 메시지가 증발하지 않고 보존됩니다.
     */
    @Bean
    public Queue model3dQueue() {
        return new Queue(MODEL3D_QUEUE, true);
    }

    /**
     * Binding: Exchange와 Queue를 연결하는 규칙 정의
     * - "model3dExchange"로 들어온 메시지 중에서
     * - 라우팅 키가 "model3d.upload"인 메시지는
     * - "model3dQueue"로 전달하겠다는 설정입니다.
     */
    @Bean
    public Binding model3dBinding(@Qualifier("model3dQueue") Queue model3dQueue, 
                                   @Qualifier("model3dExchange") TopicExchange model3dExchange) {
        return BindingBuilder.bind(model3dQueue)
                .to(model3dExchange)
                .with(MODEL3D_ROUTING_KEY);
    }

    /**
     * 3D 모델 생성 완료 응답 Queue 생성
     * - 3D 모델 생성 서버에서 생성이 완료되면 이 큐로 메시지를 보냅니다.
     * - Spring Boot 서버는 이 큐를 구독(Consume)하여 생성 완료 알림을 받습니다.
     */
    @Bean
    public Queue model3dResponseQueue() {
        return new Queue(MODEL3D_RESPONSE_QUEUE, true);
    }

    /**
     * 3D 모델 생성 완료 응답 Binding 설정
     * - "model3dExchange"로 들어온 메시지 중에서
     * - 라우팅 키가 "model3d.response"인 메시지는
     * - "model3dResponseQueue"로 전달하겠다는 설정입니다.
     */
    @Bean
    public Binding model3dResponseBinding(@Qualifier("model3dResponseQueue") Queue model3dResponseQueue, 
                                           @Qualifier("model3dExchange") TopicExchange model3dExchange) {
        return BindingBuilder.bind(model3dResponseQueue)
                .to(model3dExchange)
                .with(MODEL3D_RESPONSE_ROUTING_KEY);
    }

    // ===== VectorDB 메타데이터 업데이트 Bean 설정 =====

    /**
     * VectorDB 메타데이터 업데이트 Queue 생성
     * - 3D 모델 정보가 수정되면 VectorDB의 메타데이터도 함께 업데이트해야 합니다.
     * - Flask 서버가 이 큐를 구독하여 메타데이터 업데이트를 처리합니다.
     */
    @Bean
    public Queue model3dMetadataUpdateQueue() {
        return new Queue(MODEL3D_METADATA_UPDATE_QUEUE, true);
    }

    /**
     * VectorDB 메타데이터 업데이트 Binding 설정
     * - "model3dExchange"로 들어온 메시지 중에서
     * - 라우팅 키가 "model3d.metadata.update"인 메시지는
     * - "model3dMetadataUpdateQueue"로 전달합니다.
     */
    @Bean
    public Binding model3dMetadataUpdateBinding(@Qualifier("model3dMetadataUpdateQueue") Queue model3dMetadataUpdateQueue, 
                                                 @Qualifier("model3dExchange") TopicExchange model3dExchange) {
        return BindingBuilder.bind(model3dMetadataUpdateQueue)
                .to(model3dExchange)
                .with(MODEL3D_METADATA_UPDATE_ROUTING_KEY);
    }

    // ===== VectorDB 삭제 Bean 설정 =====

    /**
     * VectorDB 삭제 Queue 생성
     * - 3D 모델이 삭제되면 VectorDB에서도 해당 데이터를 삭제해야 합니다.
     * - Flask 서버가 이 큐를 구독하여 삭제를 처리합니다.
     */
    @Bean
    public Queue model3dDeleteQueue() {
        return new Queue(MODEL3D_DELETE_QUEUE, true);
    }

    /**
     * VectorDB 삭제 Binding 설정
     * - "model3dExchange"로 들어온 메시지 중에서
     * - 라우팅 키가 "model3d.delete"인 메시지는
     * - "model3dDeleteQueue"로 전달합니다.
     */
    @Bean
    public Binding model3dDeleteBinding(@Qualifier("model3dDeleteQueue") Queue model3dDeleteQueue, 
                                         @Qualifier("model3dExchange") TopicExchange model3dExchange) {
        return BindingBuilder.bind(model3dDeleteQueue)
                .to(model3dExchange)
                .with(MODEL3D_DELETE_ROUTING_KEY);
    }

    // ===== 추천 기능 관련 Bean 설정 =====

    /**
     * 추천 Topic Exchange 생성
     * - Topic 방식: 라우팅 키의 패턴을 보고 메시지를 배정
     */
    @Bean
    public TopicExchange recommandExchange() {
        return new TopicExchange(RECOMMAND_EXCHANGE, true, false);
    }

    /**
     * 추천 요청 Queue 생성
     * - AI 추천 서버로 보낼 추천 요청 메시지를 대기시키는 큐
     */
    @Bean
    public Queue recommandQueue() {
        return new Queue(RECOMMAND_QUEUE, true);
    }

    /**
     * 추천 요청 Binding 설정
     * - "recommandExchange"로 들어온 메시지 중에서
     * - 라우팅 키가 "recommand.request"인 메시지는
     * - "recommandQueue"로 전달합니다.
     */
    @Bean
    public Binding recommandBinding(@Qualifier("recommandQueue") Queue recommandQueue, 
                                     @Qualifier("recommandExchange") TopicExchange recommandExchange) {
        return BindingBuilder.bind(recommandQueue)
                .to(recommandExchange)
                .with(RECOMMAND_ROUTING_KEY);
    }

    /**
     * 추천 결과 응답 Queue 생성
     * - AI 추천 서버에서 분석 및 추천 결과를 받는 큐
     */
    @Bean
    public Queue recommandResponseQueue() {
        return new Queue(RECOMMAND_RESPONSE_QUEUE, true);
    }

    /**
     * 추천 결과 응답 Binding 설정
     * - "recommandExchange"로 들어온 메시지 중에서
     * - 라우팅 키가 "recommand.response"인 메시지는
     * - "recommandResponseQueue"로 전달합니다.
     */
    @Bean
    public Binding recommandResponseBinding(@Qualifier("recommandResponseQueue") Queue recommandResponseQueue, 
                                             @Qualifier("recommandExchange") TopicExchange recommandExchange) {
        return BindingBuilder.bind(recommandResponseQueue)
                .to(recommandExchange)
                .with(RECOMMAND_RESPONSE_ROUTING_KEY);
    }
}