package com.example.myroom.domain.model3D.controller;

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

import com.example.myroom.domain.model3D.dto.request.ModelDimensionsCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.ModelDimensionsUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.ModelDimensionsResponseDto;
import com.example.myroom.domain.model3D.service.ModelDimensionsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model3d/{model3dId}/dimensions")
public class ModelDimensionsController implements ModelDimensionsApi {
    private final ModelDimensionsService modelDimensionsService;

    @GetMapping("")
    public ResponseEntity<ModelDimensionsResponseDto> getModelDimensionsByModel3DId(
            @PathVariable(name = "model3dId") Long model3dId) {
        ModelDimensionsResponseDto responseDto = modelDimensionsService.getModelDimensionsByModel3DId(model3dId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ModelDimensionsResponseDto> createModelDimensions(
            @PathVariable(name = "model3dId") Long model3dId,
            @Valid @RequestBody ModelDimensionsCreateRequestDto createRequestDto) {
        // createRequestDto의 model3dId를 URL 경로의 model3dId로 설정
        ModelDimensionsCreateRequestDto adjustedDto = new ModelDimensionsCreateRequestDto(
                model3dId,
                createRequestDto.width(),
                createRequestDto.length(),
                createRequestDto.height()
        );
        ModelDimensionsResponseDto responseDto = modelDimensionsService.createModelDimensions(adjustedDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ModelDimensionsResponseDto> updateModelDimensions(
            @PathVariable(name = "model3dId") Long model3dId,
            @Valid @RequestBody ModelDimensionsUpdateRequestDto updateRequestDto) {
        // model3dId로 조회하여 해당 치수 정보를 업데이트
        ModelDimensionsResponseDto responseDto = modelDimensionsService.getModelDimensionsByModel3DId(model3dId);
        ModelDimensionsResponseDto updatedResponseDto = modelDimensionsService.updateModelDimensions(
                responseDto.id(),
                updateRequestDto
        );
        return ResponseEntity.ok(updatedResponseDto);
    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteModelDimensions(
            @PathVariable(name = "model3dId") Long model3dId) {
        ModelDimensionsResponseDto responseDto = modelDimensionsService.getModelDimensionsByModel3DId(model3dId);
        modelDimensionsService.deleteModelDimensions(responseDto.id());
        return ResponseEntity.ok().build();
    }

}
