package com.example.myroom.admin.model3D.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myroom.admin.model3D.dto.request.AdminModel3DCreateRequestDto;
import com.example.myroom.admin.model3D.dto.request.AdminModel3DUpdateRequestDto;
import com.example.myroom.admin.model3D.dto.response.AdminModel3DResponseDto;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminModel3DService {
    private final Model3DRepository model3DRepository;

    public AdminModel3DResponseDto getModel3DById(Long model3dId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));
        return AdminModel3DResponseDto.from(model3D);
    }

    public List<AdminModel3DResponseDto> getAllModel3D() {
        List<Model3D> model3DList = model3DRepository.findAll();
        return model3DList.stream()
                .map(AdminModel3DResponseDto::from)
                .toList();
    }

    public AdminModel3DResponseDto createModel3D(AdminModel3DCreateRequestDto createRequestDto) {
        Model3D model3D = Model3D.builder()
                .createdAt(LocalDateTime.now())
                .link(createRequestDto.link())
                .creatorId(createRequestDto.creatorId())
                .isShared(createRequestDto.isShared())
                .description(createRequestDto.description())
                .build();
        Model3D savedModel3D = model3DRepository.save(model3D);
        return AdminModel3DResponseDto.from(savedModel3D);
    }

    public AdminModel3DResponseDto updateModel3D(Long model3dId, AdminModel3DUpdateRequestDto updateRequestDto) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

        model3D.adminUpdate(
                updateRequestDto.link(),
                updateRequestDto.creatorId(),
                updateRequestDto.isShared(),
                updateRequestDto.description());

        Model3D updatedModel3D = model3DRepository.save(model3D);
        return AdminModel3DResponseDto.from(updatedModel3D);
    }

    public void deleteModel3D(Long model3dId) {
        if (!model3DRepository.existsById(model3dId)) {
            throw new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다.");
        }
        model3DRepository.deleteById(model3dId);
    }

}
