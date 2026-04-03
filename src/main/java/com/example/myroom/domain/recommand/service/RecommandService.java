package com.example.myroom.domain.recommand.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.image.ImageUploadService;
import com.example.myroom.domain.image.S3ImageUploadService;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.model3D.repository.Model3DRepository;
import com.example.myroom.domain.recommand.dto.message.RecommandResponseMessage;
import com.example.myroom.domain.recommand.dto.response.RecommandHistoryResponseDto;
import com.example.myroom.domain.recommand.dto.response.RecommandSimpleHistoryResponseDto;
import com.example.myroom.domain.recommand.messaging.RecommandProducer;
import com.example.myroom.domain.recommand.model.RecommandDetectedItem;
import com.example.myroom.domain.recommand.model.RecommandHistory;
import com.example.myroom.domain.recommand.model.RecommandRecommendation;
import com.example.myroom.domain.recommand.model.RecommandResult;
import com.example.myroom.domain.recommand.model.RecommandRoomAnalysis;
import com.example.myroom.domain.recommand.repository.RecommandHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
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
@Transactional(readOnly = true)
public class RecommandService {
    
    private final ImageUploadService imageUploadService;
    private final S3ImageUploadService s3ImageUploadService;
    private final RecommandProducer recommandProducer;
    private final RecommandHistoryRepository recommandHistoryRepository;
    private final MemberRepository memberRepository;
    private final Model3DRepository model3DRepository;
    private final ObjectMapper objectMapper;
    
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
            //imageUrl = imageUploadService.uploadImage(imageFile);
            imageUrl = s3ImageUploadService.uploadImage(imageFile);
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
    @Transactional
    public void saveRecommandResult(RecommandResponseMessage response) {
        RecommandResponseMessage.RecommendationData recommendation = response.getRecommendation();
        log.info("💾 추천 결과 DB 저장 시작: memberId={}, targetCategory={}", 
            response.getMemberId(), recommendation != null ? recommendation.getTargetCategory() : null);

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

            persistRecommandHistory(response);
            
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

            persistRecommandHistory(response);
            
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

    public Page<RecommandHistoryResponseDto> getMyRecommandHistories(Long memberId, Pageable pageable) {
        return recommandHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(RecommandHistoryResponseDto::from);
    }

    public RecommandHistoryResponseDto getMyRecommandHistoryById(Long memberId, Long historyId) {
        RecommandHistory history = recommandHistoryRepository.findByIdAndMemberId(historyId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("추천 이력을 찾을 수 없습니다. id=" + historyId));
        return RecommandHistoryResponseDto.from(history);
    }

    public Page<RecommandSimpleHistoryResponseDto> getMySimpleRecommandHistories(Long memberId, Pageable pageable) {
        return recommandHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(RecommandSimpleHistoryResponseDto::from);
    }

    public RecommandSimpleHistoryResponseDto getMySimpleRecommandHistoryById(Long memberId, Long historyId) {
        RecommandHistory history = recommandHistoryRepository.findByIdAndMemberId(historyId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("추천 이력을 찾을 수 없습니다. id=" + historyId));
        return RecommandSimpleHistoryResponseDto.from(history);
    }

    private void persistRecommandHistory(RecommandResponseMessage response) {
        Member member = memberRepository.findById(response.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. id=" + response.getMemberId()));

        RecommandResponseMessage.RoomAnalysis roomAnalysis = response.getRoomAnalysis();
        RecommandResponseMessage.RecommendationData recommendation = response.getRecommendation();

        RecommandHistory history = RecommandHistory.builder()
                .member(member)
                .status(response.getStatus())
                .responseTimestamp(response.getTimestamp())
                .build();

        if (roomAnalysis != null) {
            RecommandRoomAnalysis analysis = RecommandRoomAnalysis.builder()
                    .style(roomAnalysis.getStyle())
                    .color(roomAnalysis.getColor())
                    .material(roomAnalysis.getMaterial())
                    .detectedCount(roomAnalysis.getDetectedCount())
                    .build();

            if (roomAnalysis.getDetectedFurniture() != null) {
                roomAnalysis.getDetectedFurniture().stream()
                        .filter(name -> name != null && !name.isBlank())
                        .forEach(analysis::addDetectedFurniture);
            }

            if (roomAnalysis.getDetailedDetections() != null) {
                for (RecommandResponseMessage.DetectedItem item : roomAnalysis.getDetailedDetections()) {
                    RecommandDetectedItem detectedItem = RecommandDetectedItem.builder()
                            .name(item.getName())
                            .confidence(item.getConfidence())
                            .bboxX1(readBboxValue(item.getBbox(), 0, 0))
                            .bboxY1(readBboxValue(item.getBbox(), 0, 1))
                            .bboxX2(readBboxValue(item.getBbox(), 1, 0))
                            .bboxY2(readBboxValue(item.getBbox(), 1, 1))
                            .build();
                    analysis.addDetailedDetection(detectedItem);
                }
            }

            history.assignRoomAnalysis(analysis);
        }

        if (recommendation != null) {
            RecommandRecommendation recommandRecommendation = RecommandRecommendation.builder()
                    .targetCategory(recommendation.getTargetCategory())
                    .reasoning(recommendation.getReasoning())
                    .searchQuery(recommendation.getSearchQuery())
                    .resultCount(recommendation.getResultCount())
                    .build();

            if (recommendation.getResults() != null) {
                recommendation.getResults().forEach(result -> {
                    RecommandResult recommandResult = RecommandResult.builder()
                            .rankIndex(result.getRank())
                            .score(result.getScore())
                            .metadataJson(toJson(result.getMetadata()))
                            .build();

                    if (result.getModel3dId() != null) {
                        model3DRepository.findById(result.getModel3dId())
                                .ifPresent(recommandResult::addModel3D);
                    }

                    recommandRecommendation.addResult(recommandResult);
                });
            }

            history.assignRecommendation(recommandRecommendation);
        }

        recommandHistoryRepository.save(history);
    }

    private Double readBboxValue(List<List<Double>> bbox, int pointIndex, int axisIndex) {
        if (bbox == null || bbox.isEmpty()) {
            return null;
        }

        // 기본 포맷: [[x1, y1], [x2, y2]]
        if (bbox.size() > pointIndex) {
            List<Double> point = bbox.get(pointIndex);
            if (point != null && point.size() > axisIndex) {
                return point.get(axisIndex);
            }
        }

        // 호환 포맷: [[x1, y1, x2, y2]]
        List<Double> flattenedPoint = bbox.get(0);
        if (flattenedPoint == null || flattenedPoint.size() < 4) {
            return null;
        }

        if (pointIndex == 0 && axisIndex == 0) {
            return flattenedPoint.get(0);
        }
        if (pointIndex == 0 && axisIndex == 1) {
            return flattenedPoint.get(1);
        }
        if (pointIndex == 1 && axisIndex == 0) {
            return flattenedPoint.get(2);
        }
        if (pointIndex == 1 && axisIndex == 1) {
            return flattenedPoint.get(3);
        }

        return null;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("추천 결과 JSON 직렬화에 실패했습니다.", e);
        }
    }
}
