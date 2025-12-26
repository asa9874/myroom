package com.example.myroom.domain.recommand.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myroom.domain.recommand.dto.request.RoomImageUploadRequestDto;
import com.example.myroom.domain.recommand.dto.response.RoomImageResponseDto;
import com.example.myroom.domain.recommand.model.RoomImage;
import com.example.myroom.domain.recommand.repository.RoomImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomImageService {
    private final RoomImageRepository roomImageRepository;

    public RoomImageResponseDto getRoomImageById(Long roomImageId) {
        RoomImage roomImage = roomImageRepository.findById(roomImageId)
                .orElseThrow(() -> new IllegalArgumentException("방 이미지 " + roomImageId + "을 찾을 수 없습니다."));
        return RoomImageResponseDto.from(roomImage);
    }

    public List<RoomImageResponseDto> getRoomImagesByMemberId(Long memberId) {
        List<RoomImage> roomImages = roomImageRepository.findByMemberId(memberId);
        return roomImages.stream()
                .map(RoomImageResponseDto::from)
                .toList();
    }

    public List<RoomImageResponseDto> getAllRoomImages() {
        List<RoomImage> roomImages = roomImageRepository.findAll();
        return roomImages.stream()
                .map(RoomImageResponseDto::from)
                .toList();
    }

    public RoomImageResponseDto uploadRoomImage(RoomImageUploadRequestDto uploadRequestDto, Long memberId) {
        RoomImage roomImage = RoomImage.builder()
                .memberId(memberId)
                .imageUrl(uploadRequestDto.imageUrl())
                .createdAt(LocalDateTime.now())
                .build();
        
        RoomImage savedRoomImage = roomImageRepository.save(roomImage);
        return RoomImageResponseDto.from(savedRoomImage);
    }

    public void deleteRoomImage(Long roomImageId, Long memberId) {
        RoomImage roomImage = roomImageRepository.findById(roomImageId)
                .orElseThrow(() -> new IllegalArgumentException("방 이미지 " + roomImageId + "을 찾을 수 없습니다."));

        if (!roomImage.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("방 이미지를 삭제할 권한이 없습니다.");
        }

        roomImageRepository.deleteById(roomImageId);
    }

}
