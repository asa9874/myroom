package com.example.myroom.domain.recommand.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myroom.domain.recommand.dto.request.RecommendedModelCreateRequestDto;
import com.example.myroom.domain.recommand.dto.request.RecommendedModelUpdateRequestDto;
import com.example.myroom.domain.recommand.dto.response.RecommendedModelResponseDto;
import com.example.myroom.domain.recommand.model.RecommendedModel;
import com.example.myroom.domain.recommand.model.RecommendationSession;
import com.example.myroom.domain.recommand.repository.RecommendedModelRepository;
import com.example.myroom.domain.recommand.repository.RecommendationSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendedModelService {
    private final RecommendedModelRepository recommendedModelRepository;
    private final RecommendationSessionRepository recommendationSessionRepository;

    public RecommendedModelResponseDto getRecommendedModelById(Long recommendedModelId) {
        RecommendedModel recommendedModel = recommendedModelRepository.findById(recommendedModelId)
                .orElseThrow(() -> new IllegalArgumentException("추천된 모델 " + recommendedModelId + "을 찾을 수 없습니다."));
        return RecommendedModelResponseDto.from(recommendedModel);
    }

    public List<RecommendedModelResponseDto> getRecommendedModelsBySessionId(Long sessionId) {
        List<RecommendedModel> recommendedModels = recommendedModelRepository.findByRecommendationSessionId(sessionId);
        return recommendedModels.stream()
                .map(RecommendedModelResponseDto::from)
                .toList();
    }

    public List<RecommendedModelResponseDto> getAllRecommendedModels() {
        List<RecommendedModel> recommendedModels = recommendedModelRepository.findAll();
        return recommendedModels.stream()
                .map(RecommendedModelResponseDto::from)
                .toList();
    }

    public RecommendedModelResponseDto createRecommendedModel(RecommendedModelCreateRequestDto createRequestDto) {
        RecommendationSession recommendationSession = recommendationSessionRepository.findById(createRequestDto.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("추천 세션 " + createRequestDto.sessionId() + "을 찾을 수 없습니다."));

        RecommendedModel recommendedModel = RecommendedModel.builder()
                .recommendationSession(recommendationSession)
                .modelId(createRequestDto.modelId())
                .similarityScore(createRequestDto.similarityScore())
                .recommendationReason(createRequestDto.recommendationReason())
                .build();

        RecommendedModel savedRecommendedModel = recommendedModelRepository.save(recommendedModel);
        return RecommendedModelResponseDto.from(savedRecommendedModel);
    }

    public RecommendedModelResponseDto updateRecommendedModel(Long recommendedModelId, RecommendedModelUpdateRequestDto updateRequestDto) {
        RecommendedModel recommendedModel = recommendedModelRepository.findById(recommendedModelId)
                .orElseThrow(() -> new IllegalArgumentException("추천된 모델 " + recommendedModelId + "을 찾을 수 없습니다."));

        recommendedModel.update(
                updateRequestDto.similarityScore(),
                updateRequestDto.recommendationReason());

        RecommendedModel updatedRecommendedModel = recommendedModelRepository.save(recommendedModel);
        return RecommendedModelResponseDto.from(updatedRecommendedModel);
    }

    public void deleteRecommendedModel(Long recommendedModelId) {
        recommendedModelRepository.findById(recommendedModelId)
                .orElseThrow(() -> new IllegalArgumentException("추천된 모델 " + recommendedModelId + "을 찾을 수 없습니다."));

        recommendedModelRepository.deleteById(recommendedModelId);
    }

}
