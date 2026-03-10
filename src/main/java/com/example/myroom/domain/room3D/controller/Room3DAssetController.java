package com.example.myroom.domain.room3D.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.room3D.dto.response.Room3DAssetResponseDto;
import com.example.myroom.domain.room3D.service.Room3DAssetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room3d/assets")
public class Room3DAssetController implements Room3DAssetApi {

    private final Room3DAssetService room3DAssetService;

    @GetMapping("/{id}")
    public ResponseEntity<Room3DAssetResponseDto> getAssetById(
            @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(room3DAssetService.getAssetById(id));
    }

    @GetMapping
    public ResponseEntity<List<Room3DAssetResponseDto>> getAllAssets() {
        return ResponseEntity.ok(room3DAssetService.getAllAssets());
    }
}
