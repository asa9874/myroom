package com.example.myroom.domain.recommand.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.recommand.dto.request.RecommendationSessionCreateRequestDto;
import com.example.myroom.domain.recommand.dto.request.RecommendationSessionUpdateRequestDto;
import com.example.myroom.domain.recommand.dto.response.RecommendationSessionResponseDto;
import com.example.myroom.domain.recommand.service.RecommendationSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationSessionController implements RecommendationSessionApi {
    private final RecommendationSessionService recommendationSessionService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecommendationSessionResponseDto> createRecommendationSession(
            @Valid @RequestBody RecommendationSessionCreateRequestDto createRequestDto) {
        RecommendationSessionResponseDto responseDto = recommendationSessionService.createRecommendationSession(createRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<RecommendationSessionResponseDto> getRecommendationSessionById(
            @PathVariable(name = "sessionId") Long sessionId) {
        RecommendationSessionResponseDto responseDto = recommendationSessionService.getRecommendationSessionById(sessionId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{sessionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecommendationSessionResponseDto> updateRecommendationSession(
            @PathVariable(name = "sessionId") Long sessionId,
            @Valid @RequestBody RecommendationSessionUpdateRequestDto updateRequestDto) {
        RecommendationSessionResponseDto responseDto = recommendationSessionService.updateRecommendationSession(sessionId, updateRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRecommendationSession(
            @PathVariable(name = "sessionId") Long sessionId) {
        recommendationSessionService.deleteRecommendationSession(sessionId);
        return ResponseEntity.ok().build();
    }

}
