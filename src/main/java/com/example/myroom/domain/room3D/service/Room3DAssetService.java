package com.example.myroom.domain.room3D.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myroom.domain.room3D.dto.response.Room3DAssetResponseDto;
import com.example.myroom.domain.room3D.model.Room3DAsset;
import com.example.myroom.domain.room3D.repository.Room3DAssetRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Room3DAssetService {

    private final Room3DAssetRepository room3DAssetRepository;

    public Room3DAssetResponseDto getAssetById(Long id) {
        Room3DAsset asset = room3DAssetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("에셋을 찾을 수 없습니다. id=" + id));
        return Room3DAssetResponseDto.from(asset);
    }

    public List<Room3DAssetResponseDto> getAllAssets() {
        return room3DAssetRepository.findAll().stream()
                .map(Room3DAssetResponseDto::from)
                .toList();
    }
}
