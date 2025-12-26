package com.example.myroom.admin.model3D.controller;

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

import com.example.myroom.admin.model3D.dto.request.AdminModel3DCreateRequestDto;
import com.example.myroom.admin.model3D.dto.request.AdminModel3DUpdateRequestDto;
import com.example.myroom.admin.model3D.dto.response.AdminModel3DResponseDto;
import com.example.myroom.admin.model3D.service.AdminModel3DService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/model3d")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminModel3DController implements AdminModel3DApi {
    private final AdminModel3DService adminModel3DService;

    @GetMapping("/{model3dId}")
    public ResponseEntity<AdminModel3DResponseDto> getModel3DById(
            @PathVariable(name = "model3dId") Long model3dId) {
        AdminModel3DResponseDto responseDto = adminModel3DService.getModel3DById(model3dId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<AdminModel3DResponseDto>> getAllModel3D() {
        List<AdminModel3DResponseDto> model3DList = adminModel3DService.getAllModel3D();
        return ResponseEntity.ok(model3DList);
    }

    @PostMapping("/")
    public ResponseEntity<AdminModel3DResponseDto> createModel3D(
            @Valid @RequestBody AdminModel3DCreateRequestDto createRequestDto) {
        AdminModel3DResponseDto responseDto = adminModel3DService.createModel3D(createRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{model3dId}")
    public ResponseEntity<AdminModel3DResponseDto> updateModel3D(
            @PathVariable(name = "model3dId") Long model3dId,
            @Valid @RequestBody AdminModel3DUpdateRequestDto updateRequestDto) {
        AdminModel3DResponseDto responseDto = adminModel3DService.updateModel3D(model3dId, updateRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{model3dId}")
    public ResponseEntity<Void> deleteModel3D(
            @PathVariable(name = "model3dId") Long model3dId) {
        adminModel3DService.deleteModel3D(model3dId);
        return ResponseEntity.ok().build();
    }

}
