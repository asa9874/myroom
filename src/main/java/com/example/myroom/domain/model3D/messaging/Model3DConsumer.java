package com.example.myroom.domain.model3D.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.myroom.domain.model3D.dto.message.Model3DGenerationResponse;
import com.example.myroom.domain.model3D.service.Model3DService;
import com.example.myroom.global.config.RabbitConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 3D 모델 생성 완료 메시지 수신
 * - 3D 모델 생성 서버에서 보낸 메시지를 소비(Consume)합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Model3DConsumer {

    private final Model3DService model3DService;

    /**
     * 3D 모델 생성 완료 메시지 처리
     * 
     * @RabbitListener: 지정된 큐를 구독하여 메시지가 도착하면 자동으로 이 메서드를 실행합니다.
     * - queues: 구독할 큐 이름 지정
     * - Spring Boot가 자동으로 JSON 메시지를 Model3DGenerationResponse 객체로 변환해줍니다.
     * 
     * @param response 3D 모델 생성 서버로부터 받은 응답 메시지
     */
    @RabbitListener(queues = RabbitConfig.MODEL3D_RESPONSE_QUEUE)
    public void handleModel3DGenerationResponse(Model3DGenerationResponse response) {
        log.info("========================================");
        log.info("3D 모델 생성 완료 메시지 수신");
        log.info("========================================");
        log.info("회원 ID: {}", response.getMemberId());
        log.info("원본 이미지 URL: {}", response.getOriginalImageUrl());
        log.info("생성된 3D 모델 URL: {}", response.getModel3dUrl());
        log.info("썸네일 URL: {}", response.getThumbnailUrl());
        log.info("생성 상태: {}", response.getStatus());
        log.info("상태 메시지: {}", response.getMessage());
        log.info("처리 시간: {}초", response.getProcessingTimeSeconds());
        log.info("생성 완료 시각: {}", response.getTimestamp());
        log.info("========================================");

        try {
            // 생성 상태에 따라 분기 처리
            if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
                // 성공: 3D 모델 정보를 DB에 저장
                log.info("✅ 3D 모델 생성 성공 - DB 저장 시작");
                model3DService.saveGeneratedModel(response);
                log.info("✅ DB 저장 완료");
                
            } else if ("FAILED".equalsIgnoreCase(response.getStatus())) {
                // 실패: 에러 로그 기록 및 알림 처리
                log.error("❌ 3D 모델 생성 실패 - 회원 ID: {}, 에러: {}", 
                    response.getMemberId(), response.getMessage());
                model3DService.handleGenerationFailure(response);
                
            } else {
                // 기타 상태 (PROCESSING 등)
                log.warn("⚠️ 알 수 없는 상태: {}", response.getStatus());
            }
            
        } catch (Exception e) {
            // 메시지 처리 중 에러 발생 시 로그 기록
            log.error("❌ 3D 모델 생성 응답 처리 중 오류 발생", e);
            log.error("에러 메시지: {}", e.getMessage());
            log.error("응답 데이터: {}", response);
            
            // 실제 운영 환경에서는:
            // 1. Dead Letter Queue로 메시지 이동
            // 2. 알림 서비스를 통해 관리자에게 알림
            // 3. 재시도 로직 구현
        }
    }

    /**
     * 메시지 수신 확인용 간단한 로그 메서드
     * - 디버깅이나 모니터링 목적으로 사용
     */
    private void logMessageReceived(Model3DGenerationResponse response) {
        log.info("📩 새로운 3D 모델 생성 완료 메시지 수신: memberId={}, status={}", 
            response.getMemberId(), response.getStatus());
    }
}
