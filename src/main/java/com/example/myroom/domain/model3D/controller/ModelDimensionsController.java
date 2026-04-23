package com.example.myroom.domain.model3D.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.model3D.dto.request.ModelDimensionsCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.ModelDimensionsUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.ModelDimensionsResponseDto;
import com.example.myroom.domain.model3D.service.ModelDimensionsService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model3ds/{model3dId}/dimensions")
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

    @PostMapping(value = "/request-by-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> requestModelDimensionsByImage(
            @PathVariable(name = "model3dId") Long model3dId,
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @AuthenticationPrincipal CustomUserDetails member) {
        String message = modelDimensionsService.requestModelDimensionsByImage(model3dId, imageFile, member.getId());
        return ResponseEntity.ok(message);
    }

    @PutMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ModelDimensionsResponseDto> updateModelDimensions(
            @PathVariable(name = "model3dId") Long model3dId,
            @Valid @RequestBody ModelDimensionsUpdateRequestDto updateRequestDto) {
        // exists 체크 후 업데이트 또는 생성
        ModelDimensionsResponseDto responseDto = modelDimensionsService.updateModelDimensionsByModel3dId(
                model3dId,
                updateRequestDto
        );
        return ResponseEntity.ok(responseDto);
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
