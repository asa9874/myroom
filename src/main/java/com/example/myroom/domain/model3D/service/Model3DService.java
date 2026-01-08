package com.example.myroom.domain.model3D.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.image.ImageUploadService;
import com.example.myroom.domain.model3D.dto.request.Model3DCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.domain.model3D.messaging.Model3DProducer;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Model3DService {
    private final Model3DRepository model3DRepository;
    private final ImageUploadService imageUploadService;
    private final Model3DProducer model3DProducer;

    public Model3DResponseDto getModel3DById(Long model3dId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));
        return Model3DResponseDto.from(model3D);
    }

    public List<Model3DResponseDto> getAllModel3D() {
        List<Model3D> model3DList = model3DRepository.findAll();
        return model3DList.stream()
                .map(Model3DResponseDto::from)
                .toList();
    }

    public Model3DResponseDto createModel3D(Model3DCreateRequestDto createRequestDto, Long creatorId) {
        Model3D model3D = Model3D.builder()
                .createdAt(LocalDateTime.now())
                .link(createRequestDto.link())
                .creatorId(creatorId)
                .isShared(createRequestDto.isShared())
                .description(createRequestDto.description())
                .build();
        Model3D savedModel3D = model3DRepository.save(model3D);
        return Model3DResponseDto.from(savedModel3D);
    }

    public Model3DResponseDto updateModel3D(Long model3dId, Model3DUpdateRequestDto updateRequestDto, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

        if (!model3D.getCreatorId().equals(memberId)) {
            throw new IllegalArgumentException("3D 모델을 수정할 권한이 없습니다.");
        }

        model3D.update(
                updateRequestDto.link(),
                updateRequestDto.isShared(),
                updateRequestDto.description());

        Model3D updatedModel3D = model3DRepository.save(model3D);
        return Model3DResponseDto.from(updatedModel3D);
    }

    public void deleteModel3D(Long model3dId, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

        if (!model3D.getCreatorId().equals(memberId)) {
            throw new IllegalArgumentException("3D 모델을 삭제할 권한이 없습니다.");
        }

        model3DRepository.deleteById(model3dId);
    }

    public String uploadModel3DFile(MultipartFile file, Long memberId) {
        String imageUrl;
        try {
            imageUrl = imageUploadService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        // RabbitMQ로 메시지 전송
        model3DProducer.sendModel3DUploadMessage(imageUrl, memberId);

        return imageUrl;
    }
}
