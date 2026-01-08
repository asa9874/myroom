package com.example.myroom.domain.model3D.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.model3D.dto.request.Model3DCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.domain.model3D.service.Model3DService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model3d")
public class Model3DController implements Model3DApi {
    private final Model3DService model3DService;

    @GetMapping("/{model3dId}")
    public ResponseEntity<Model3DResponseDto> getModel3DById(
            @PathVariable(name = "model3dId") Long model3dId) {
        Model3DResponseDto responseDto = model3DService.getModel3DById(model3dId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<Model3DResponseDto>> getAllModel3D() {
        List<Model3DResponseDto> model3DList = model3DService.getAllModel3D();
        return ResponseEntity.ok(model3DList);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Model3DResponseDto> createModel3D(
            @Valid @RequestBody Model3DCreateRequestDto createRequestDto,
            @AuthenticationPrincipal CustomUserDetails member) {
        Model3DResponseDto responseDto = model3DService.createModel3D(createRequestDto, member.getId());
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadModel3DFile(
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @AuthenticationPrincipal CustomUserDetails member) {
        String fileUrl = model3DService.uploadModel3DFile(imageFile, member.getId());
        return ResponseEntity.ok(fileUrl);
    }
    
}
