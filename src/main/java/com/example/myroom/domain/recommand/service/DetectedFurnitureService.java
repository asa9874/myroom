package com.example.myroom.domain.recommand.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myroom.domain.recommand.dto.request.DetectedFurnitureCreateRequestDto;
import com.example.myroom.domain.recommand.dto.response.DetectedFurnitureResponseDto;
import com.example.myroom.domain.recommand.model.DetectedFurniture;
import com.example.myroom.domain.recommand.model.RoomImage;
import com.example.myroom.domain.recommand.repository.DetectedFurnitureRepository;
import com.example.myroom.domain.recommand.repository.RoomImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetectedFurnitureService {
    private final DetectedFurnitureRepository detectedFurnitureRepository;
    private final RoomImageRepository roomImageRepository;

    public DetectedFurnitureResponseDto getDetectedFurnitureById(Long detectedFurnitureId) {
        DetectedFurniture detectedFurniture = detectedFurnitureRepository.findById(detectedFurnitureId)
                .orElseThrow(() -> new IllegalArgumentException("탐지된 가구 " + detectedFurnitureId + "을 찾을 수 없습니다."));
        return DetectedFurnitureResponseDto.from(detectedFurniture);
    }

    public List<DetectedFurnitureResponseDto> getDetectedFurnitureByRoomImageId(Long roomImageId) {
        List<DetectedFurniture> detectedFurnitures = detectedFurnitureRepository.findByRoomImageId(roomImageId);
        return detectedFurnitures.stream()
                .map(DetectedFurnitureResponseDto::from)
                .toList();
    }

    public List<DetectedFurnitureResponseDto> getAllDetectedFurnitures() {
        List<DetectedFurniture> detectedFurnitures = detectedFurnitureRepository.findAll();
        return detectedFurnitures.stream()
                .map(DetectedFurnitureResponseDto::from)
                .toList();
    }

    public DetectedFurnitureResponseDto createDetectedFurniture(DetectedFurnitureCreateRequestDto createRequestDto) {
        RoomImage roomImage = roomImageRepository.findById(createRequestDto.roomImageId())
                .orElseThrow(() -> new IllegalArgumentException("방 이미지 " + createRequestDto.roomImageId() + "을 찾을 수 없습니다."));

        DetectedFurniture detectedFurniture = DetectedFurniture.builder()
                .roomImage(roomImage)
                .category(createRequestDto.category())
                .confidence(createRequestDto.confidence())
                .positionMetadata(createRequestDto.positionMetadata())
                .build();

        DetectedFurniture savedDetectedFurniture = detectedFurnitureRepository.save(detectedFurniture);
        return DetectedFurnitureResponseDto.from(savedDetectedFurniture);
    }

}
