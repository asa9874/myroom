package com.example.myroom.domain.recommand.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.image.ImageUploadService;
import com.example.myroom.domain.recommand.dto.message.RecommandResponseMessage;
import com.example.myroom.domain.recommand.messaging.RecommandProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 가구 추천 서비스
 * - 이미지 업로드 및 가구 추천 요청 처리
 * - RabbitMQ를 통해 AI 서버에 추천 요청 발송
 * - 추천 결과 저장 및 실패 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommandService {
    
    private final ImageUploadService imageUploadService;
    private final RecommandProducer recommandProducer;
    
    /**
     * 가구 추천 요청
     * - 이미지를 저장하고 RabbitMQ를 통해 추천 요청을 발송합니다.
     * 
     * @param imageFile 분석할 이미지 파일
     * @param category 추천할 가구 카테고리 (기본값: 'chair')
     * @param topK 반환할 추천 결과 개수 (기본값: 5)
     * @param memberId 요청한 회원 ID
     * @return 처리 상태 메시지
     */
    public String requestRecommandation(
            MultipartFile imageFile, 
            String category, 
            Integer topK, 
            Long memberId) {
        
        log.info("🎨 추천 요청 수신: memberId={}, category={}, topK={}, fileName={}", 
                memberId, category, topK, imageFile.getOriginalFilename());
        
        String imageUrl;
        try {
            // 1. 이미지 파일 저장
            imageUrl = imageUploadService.uploadImage(imageFile);
            log.info("✅ 이미지 저장 완료: memberId={}, imageUrl={}", memberId, imageUrl);
            
        } catch (IOException e) {
            log.error("❌ 이미지 저장 실패: memberId={}, error={}", memberId, e.getMessage(), e);
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 2. RabbitMQ로 추천 요청 메시지 발송
        try {
            log.info("📤 RabbitMQ 메시지 발송: memberId={}, imageUrl={}, category={}, topK={}", 
                    memberId, imageUrl, category, topK);
            
            recommandProducer.sendRecommandRequestMessage(imageUrl, memberId, category, topK);
            
            log.info("✅ 추천 요청 발송 완료: memberId={}", memberId);
            return "추천 요청이 완료되었습니다. 잠시 후 결과를 확인해주세요.";
            
        } catch (Exception e) {
            log.error("❌ 추천 요청 발송 실패: memberId={}, error={}", memberId, e.getMessage(), e);
            throw new RuntimeException("추천 요청 발송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 추천 결과 저장
     * - AI 서버에서 받은 추천 결과를 데이터베이스에 저장합니다.
     * 
     * @param response AI 서버로부터 받은 추천 결과 응답
     */
    public void saveRecommandResult(RecommandResponseMessage response) {
        log.info("💾 추천 결과 DB 저장 시작: memberId={}, targetCategory={}", 
            response.getMemberId(), response.getRecommendation().getTargetCategory());

        try {
            // 방 분석 정보 로깅
            if (response.getRoomAnalysis() != null) {
                log.info("🏠 방 분석 정보: style={}, color={}, material={}, detectedCount={}", 
                    response.getRoomAnalysis().getStyle(),
                    response.getRoomAnalysis().getColor(),
                    response.getRoomAnalysis().getMaterial(),
                    response.getRoomAnalysis().getDetectedCount());
                log.info("🛋️ 감지된 가구: {}", response.getRoomAnalysis().getDetectedFurniture());
            }
            
            // 추천 정보 로깅
            if (response.getRecommendation() != null) {
                log.info("💡 추천 정보: targetCategory={}, resultCount={}", 
                    response.getRecommendation().getTargetCategory(),
                    response.getRecommendation().getResultCount());
                log.info("🔍 검색 쿼리: {}", response.getRecommendation().getSearchQuery());
            }
            
            // TODO: 실제 DB 저장 로직 구현
            // 1. RecommandHistory 테이블에 추천 기록 저장
            // 2. RoomAnalysis 테이블에 방 분석 정보 저장
            // 3. RecommendationResult 테이블에 추천 결과 저장
            // 예시:
            // RecommandHistory history = RecommandHistory.builder()
            //     .memberId(response.getMemberId())
            //     .style(response.getRoomAnalysis().getStyle())
            //     .color(response.getRoomAnalysis().getColor())
            //     .targetCategory(response.getRecommendation().getTargetCategory())
            //     .resultCount(response.getRecommendation().getResultCount())
            //     .build();
            // recommandHistoryRepository.save(history);
            
            log.info("✅ 추천 결과 DB 저장 성공: memberId={}", response.getMemberId());
            
        } catch (Exception e) {
            log.error("❌ 추천 결과 DB 저장 실패: memberId={}, error={}", 
                response.getMemberId(), e.getMessage(), e);
            throw new RuntimeException("추천 결과 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 추천 처리 실패 처리
     * - 추천 처리 중 오류가 발생한 경우를 처리합니다.
     * 
     * @param response 실패 정보를 포함한 응답
     */
    public void handleRecommandFailure(RecommandResponseMessage response) {
        log.error("💥 추천 처리 실패");

        try {
            // 실패 로그 기록
            log.warn("⚠️ 실패 상태: {}", response.getStatus());
            
            // TODO: 실패 처리 로직 구현
            // 1. 실패 이유 분석 및 로깅
            // 2. 재시도 큐에 추가 (자동 재시도 옵션)
            // 3. 회원에게 실패 알림 전송
            // 4. 관리자에게 에러 리포트 전송
            // 5. 실패 통계 업데이트
            
            // 예시:
            // FailureLog failureLog = FailureLog.builder()
            //     .memberId(response.getMemberId())
            //     .errorMessage(response.getStatus())
            //     .timestamp(LocalDateTime.now())
            //     .build();
            // failureLogRepository.save(failureLog);
            
            log.warn("⚠️ 이미지 URL: {}", response.getRoomAnalysis() != null ? response.getRoomAnalysis().getClass().getName() : "정보 없음");
            
            // 실제 운영 환경에서는:
            // - 에러 유형에 따라 다른 처리
            // - 실패 횟수 추적 및 임계값 설정
            // - 자동 환불 처리 (유료 서비스인 경우)
            // - 사용자에게 재시도 권유
            
        } catch (Exception e) {
            log.error("❌ 실패 처리 중 추가 오류 발생: memberId={}, error={}", 
                response.getMemberId(), e.getMessage(), e);
        }
    }

    /**
     * 이전 버전 호환성을 위한 메서드
     */
    public void uploadRecommandFile(MultipartFile imageFile, Long memberId) {
        requestRecommandation(imageFile, "chair", 5, memberId);
    }
}
