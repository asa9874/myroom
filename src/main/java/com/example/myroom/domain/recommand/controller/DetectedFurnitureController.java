package com.example.myroom.domain.recommand.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.recommand.dto.request.DetectedFurnitureCreateRequestDto;
import com.example.myroom.domain.recommand.dto.response.DetectedFurnitureResponseDto;
import com.example.myroom.domain.recommand.service.DetectedFurnitureService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations/{sessionId}/detected-furnitures")
public class DetectedFurnitureController implements DetectedFurnitureApi {
    private final DetectedFurnitureService detectedFurnitureService;

    @GetMapping("/{detectedFurnitureId}")
    public ResponseEntity<DetectedFurnitureResponseDto> getDetectedFurnitureById(
            @PathVariable(name = "sessionId") Long sessionId,
            @PathVariable(name = "detectedFurnitureId") Long detectedFurnitureId) {
        DetectedFurnitureResponseDto responseDto = detectedFurnitureService.getDetectedFurnitureById(detectedFurnitureId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("")
    public ResponseEntity<List<DetectedFurnitureResponseDto>> getDetectedFurnitureByRoomImageId(
            @PathVariable(name = "sessionId") Long sessionId) {
        List<DetectedFurnitureResponseDto> responseDto = detectedFurnitureService.getAllDetectedFurnitures();
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DetectedFurnitureResponseDto> createDetectedFurniture(
            @PathVariable(name = "sessionId") Long sessionId,
            @Valid @RequestBody DetectedFurnitureCreateRequestDto createRequestDto) {
        DetectedFurnitureResponseDto responseDto = detectedFurnitureService.createDetectedFurniture(createRequestDto);
        return ResponseEntity.ok(responseDto);
    }

}
