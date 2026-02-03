package com.example.myroom.domain.model3D.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUploadRequestDto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.domain.model3D.service.Model3DService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model3ds")
public class Model3DController implements Model3DApi {
    private final Model3DService model3DService;

    @GetMapping("/{model3dId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Model3DResponseDto> getModel3DById(
            @PathVariable(name = "model3dId") Long model3dId,
            @AuthenticationPrincipal CustomUserDetails member) {
        Model3DResponseDto responseDto = model3DService.getModel3DById(model3dId, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{model3dId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Model3DResponseDto> updateModel3D(
            @PathVariable(name = "model3dId") Long model3dId,
            @Valid @RequestBody Model3DUpdateRequestDto updateRequestDto,
            @AuthenticationPrincipal CustomUserDetails member) {
        Model3DResponseDto responseDto = model3DService.updateModel3D(model3dId, updateRequestDto, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{model3dId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteModel3D(
            @PathVariable(name = "model3dId") Long model3dId,
            @AuthenticationPrincipal CustomUserDetails member) {
        model3DService.deleteModel3D(model3dId, member.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAllModel3Ds(
            @AuthenticationPrincipal CustomUserDetails member) {
        model3DService.deleteAllModel3Ds(member.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadModel3DFile(
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @RequestParam(value = "furniture_type", required = true) String furnitureType,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "is_shared", required = false) Boolean isShared,
            @AuthenticationPrincipal CustomUserDetails member) {
        Model3DUploadRequestDto uploadRequestDto = new Model3DUploadRequestDto(furnitureType, name, description, isShared);
        String fileUrl = model3DService.uploadModel3DFile(imageFile, uploadRequestDto, member.getId());
        return ResponseEntity.ok(fileUrl);
    }

    @PostMapping(value = "/upload-simple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadSimpleModel3DFile(
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @AuthenticationPrincipal CustomUserDetails member) {
        // 기본값 설정
        String furnitureType = "others";
        String name = "Temp_name_" + System.currentTimeMillis();
        String description = "임시 설명";
        Boolean isShared = false;
        
        Model3DUploadRequestDto uploadRequestDto = new Model3DUploadRequestDto(furnitureType, name, description, isShared);
        String fileUrl = model3DService.uploadModel3DFile(imageFile, uploadRequestDto, member.getId());
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Model3DResponseDto>> getAllModel3Ds(
            @AuthenticationPrincipal CustomUserDetails member) {
        List<Model3DResponseDto> responseDtos = model3DService.getAllModel3Ds(member.getId());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/member/{memberId}/search")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<Model3DResponseDto>> getModel3DsByMemberId(
            @PathVariable(name = "memberId") Long memberId,
            @AuthenticationPrincipal CustomUserDetails member,
            @RequestParam(required = false, name = "name") String name,
            Pageable pageable) {
        Page<Model3DResponseDto> responseDtos = model3DService.getModel3DsByMemberId(memberId, member.getId(), name, pageable);
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/shared/search")
    public ResponseEntity<Page<Model3DResponseDto>> getSharedModel3Ds(
            @AuthenticationPrincipal CustomUserDetails member,
            @RequestParam(required = false, name = "name") String name,
            Pageable pageable) {
        Page<Model3DResponseDto> responseDtos = model3DService.getSharedModel3Ds(member.getId(), name, pageable);
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/untrained")
    public ResponseEntity<List<Model3DResponseDto>> getNotVectorDbTrainedModel3Ds() {
        List<Model3DResponseDto> responseDtos = model3DService.getNotVectorDbTrainedModel3Ds();
        return ResponseEntity.ok(responseDtos);
    }
}
