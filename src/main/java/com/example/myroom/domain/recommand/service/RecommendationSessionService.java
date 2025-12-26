package com.example.myroom.domain.recommand.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myroom.domain.recommand.dto.request.RecommendationSessionCreateRequestDto;
import com.example.myroom.domain.recommand.dto.request.RecommendationSessionUpdateRequestDto;
import com.example.myroom.domain.recommand.dto.response.RecommendationSessionResponseDto;
import com.example.myroom.domain.recommand.model.RecommendationSession;
import com.example.myroom.domain.recommand.model.RoomImage;
import com.example.myroom.domain.recommand.repository.RecommendationSessionRepository;
import com.example.myroom.domain.recommand.repository.RoomImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationSessionService {
    private final RecommendationSessionRepository recommendationSessionRepository;
    private final RoomImageRepository roomImageRepository;

    public RecommendationSessionResponseDto getRecommendationSessionById(Long recommendationSessionId) {
        RecommendationSession recommendationSession = recommendationSessionRepository.findById(recommendationSessionId)
                .orElseThrow(() -> new IllegalArgumentException("추천 세션 " + recommendationSessionId + "을 찾을 수 없습니다."));
        return RecommendationSessionResponseDto.from(recommendationSession);
    }

    public RecommendationSessionResponseDto getRecommendationSessionByRoomImageId(Long roomImageId) {
        RecommendationSession recommendationSession = recommendationSessionRepository.findByRoomImageId(roomImageId)
                .orElseThrow(() -> new IllegalArgumentException("방 이미지 " + roomImageId + "의 추천 세션을 찾을 수 없습니다."));
        return RecommendationSessionResponseDto.from(recommendationSession);
    }

    public List<RecommendationSessionResponseDto> getAllRecommendationSessions() {
        List<RecommendationSession> recommendationSessions = recommendationSessionRepository.findAll();
        return recommendationSessions.stream()
                .map(RecommendationSessionResponseDto::from)
                .toList();
    }

    public RecommendationSessionResponseDto createRecommendationSession(RecommendationSessionCreateRequestDto createRequestDto) {
        RoomImage roomImage = roomImageRepository.findById(createRequestDto.roomImageId())
                .orElseThrow(() -> new IllegalArgumentException("방 이미지 " + createRequestDto.roomImageId() + "을 찾을 수 없습니다."));

        RecommendationSession recommendationSession = RecommendationSession.builder()
                .roomImage(roomImage)
                .styleCaption(createRequestDto.styleCaption())
                .aiDesignerAdvice(createRequestDto.aiDesignerAdvice())
                .build();

        RecommendationSession savedRecommendationSession = recommendationSessionRepository.save(recommendationSession);
        return RecommendationSessionResponseDto.from(savedRecommendationSession);
    }

    public RecommendationSessionResponseDto updateRecommendationSession(Long recommendationSessionId, RecommendationSessionUpdateRequestDto updateRequestDto) {
        RecommendationSession recommendationSession = recommendationSessionRepository.findById(recommendationSessionId)
                .orElseThrow(() -> new IllegalArgumentException("추천 세션 " + recommendationSessionId + "을 찾을 수 없습니다."));

        recommendationSession.update(
                updateRequestDto.styleCaption(),
                updateRequestDto.aiDesignerAdvice());

        RecommendationSession updatedRecommendationSession = recommendationSessionRepository.save(recommendationSession);
        return RecommendationSessionResponseDto.from(updatedRecommendationSession);
    }

    public void deleteRecommendationSession(Long recommendationSessionId) {
        recommendationSessionRepository.findById(recommendationSessionId)
                .orElseThrow(() -> new IllegalArgumentException("추천 세션 " + recommendationSessionId + "을 찾을 수 없습니다."));

        recommendationSessionRepository.deleteById(recommendationSessionId);
    }

}
