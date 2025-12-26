package com.example.myroom.domain.recommand.controller;

import java.util.List;

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

import com.example.myroom.domain.recommand.dto.request.RecommendedModelCreateRequestDto;
import com.example.myroom.domain.recommand.dto.request.RecommendedModelUpdateRequestDto;
import com.example.myroom.domain.recommand.dto.response.RecommendedModelResponseDto;
import com.example.myroom.domain.recommand.service.RecommendedModelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations/{sessionId}/recommended-models")
public class RecommendedModelController implements RecommendedModelApi {
    private final RecommendedModelService recommendedModelService;

    @GetMapping("")
    public ResponseEntity<List<RecommendedModelResponseDto>> getRecommendedModelsBySessionId(
            @PathVariable(name = "sessionId") Long sessionId) {
        List<RecommendedModelResponseDto> responseDto = recommendedModelService.getRecommendedModelsBySessionId(sessionId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{recommendedModelId}")
    public ResponseEntity<RecommendedModelResponseDto> getRecommendedModelById(
            @PathVariable(name = "sessionId") Long sessionId,
            @PathVariable(name = "recommendedModelId") Long recommendedModelId) {
        RecommendedModelResponseDto responseDto = recommendedModelService.getRecommendedModelById(recommendedModelId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecommendedModelResponseDto> createRecommendedModel(
            @PathVariable(name = "sessionId") Long sessionId,
            @Valid @RequestBody RecommendedModelCreateRequestDto createRequestDto) {
        RecommendedModelResponseDto responseDto = recommendedModelService.createRecommendedModel(createRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{recommendedModelId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RecommendedModelResponseDto> updateRecommendedModel(
            @PathVariable(name = "sessionId") Long sessionId,
            @PathVariable(name = "recommendedModelId") Long recommendedModelId,
            @Valid @RequestBody RecommendedModelUpdateRequestDto updateRequestDto) {
        RecommendedModelResponseDto responseDto = recommendedModelService.updateRecommendedModel(recommendedModelId, updateRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{recommendedModelId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRecommendedModel(
            @PathVariable(name = "sessionId") Long sessionId,
            @PathVariable(name = "recommendedModelId") Long recommendedModelId) {
        recommendedModelService.deleteRecommendedModel(recommendedModelId);
        return ResponseEntity.ok().build();
    }

}
