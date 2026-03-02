package com.example.myroom.domain.model3D.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.image.ImageUploadService;
import com.example.myroom.domain.image.S3ImageUploadService;
import com.example.myroom.domain.model3D.dto.message.Model3DGenerationResponse;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestV2Dto;
import com.example.myroom.domain.model3D.dto.request.Model3DUploadRequestDto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.domain.model3D.messaging.Model3DProducer;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Model3DService {
    private final Model3DRepository model3DRepository;
    private final ImageUploadService imageUploadService;
    private final S3ImageUploadService s3ImageUploadService;
    private final Model3DProducer model3DProducer;

    public Model3DResponseDto getModel3DById(Long model3dId, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));
        if (!isOwner(model3D.getCreatorId(), memberId)) {
            throw new IllegalArgumentException("3D 모델에 접근할 권한이 없습니다.");
        }
        return Model3DResponseDto.from(model3D);
    }

    public Model3DResponseDto updateModel3D(Long model3dId, Model3DUpdateRequestDto updateRequestDto, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

        if (!isOwner(model3D.getCreatorId(), memberId)) {
            throw new IllegalArgumentException("3D 모델을 수정할 권한이 없습니다.");
        }

        model3D.update(
                updateRequestDto.name(),
                updateRequestDto.isShared(),
                updateRequestDto.description(),
                null);

        Model3D updatedModel3D = model3DRepository.save(model3D);
        
        // VectorDB에 학습된 모델인 경우에만 메타데이터 업데이트 메시지 발송
        if (updatedModel3D.getIsVectorDbTrained()) {
            model3DProducer.sendMetadataUpdateMessage(
                    updatedModel3D.getId(),
                    memberId,
                    updatedModel3D.getName(),
                    updatedModel3D.getDescription(),
                    updatedModel3D.getIsShared()
            );
            log.info("📤 VectorDB 메타데이터 업데이트 요청: model3dId={}", model3dId);
        }
        
        return Model3DResponseDto.from(updatedModel3D);
    }

    public Model3DResponseDto updateModel3Dv2(Long model3dId, Model3DUpdateRequestV2Dto updateRequestDto, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

        if (!isOwner(model3D.getCreatorId(), memberId)) {
            throw new IllegalArgumentException("3D 모델을 수정할 권한이 없습니다.");
        }

        model3D.updateWithLink(
                updateRequestDto.name(),
                updateRequestDto.isShared(),
                updateRequestDto.description(),
                updateRequestDto.link(),
                null);

        Model3D updatedModel3D = model3DRepository.save(model3D);
        
        // VectorDB에 학습된 모델인 경우에만 메타데이터 업데이트 메시지 발송
        if (updatedModel3D.getIsVectorDbTrained()) {
            model3DProducer.sendMetadataUpdateMessage(
                    updatedModel3D.getId(),
                    memberId,
                    updatedModel3D.getName(),
                    updatedModel3D.getDescription(),
                    updatedModel3D.getIsShared()
            );
            log.info("📤 VectorDB 메타데이터 업데이트 요청: model3dId={}", model3dId);
        }
        
        return Model3DResponseDto.from(updatedModel3D);
    }

    public void deleteModel3D(Long model3dId, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

        if (!model3D.getCreatorId().equals(memberId)) {
            throw new IllegalArgumentException("3D 모델을 삭제할 권한이 없습니다.");
        }

        // VectorDB에 학습된 모델인 경우 삭제 메시지 발송
        if (model3D.getIsVectorDbTrained()) {
            model3DProducer.sendDeleteMessage(List.of(model3dId), memberId);
            log.info("🗑️ VectorDB 삭제 요청: model3dId={}", model3dId);
        }

        model3DRepository.deleteById(model3dId);
    }

    public String uploadModel3DFile(MultipartFile file, Model3DUploadRequestDto uploadRequestDto, Long memberId) {
        String imageUrl;
        try { 
            //imageUrl = imageUploadService.uploadImage(file); //로컬저장
            imageUrl = s3ImageUploadService.uploadImage(file); // S3저장
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        // 먼저 Model3D를 link=null로 저장
        Model3D model3D = Model3D.builder()
                .name(uploadRequestDto.name())
                .furnitureType(uploadRequestDto.furnitureType())
                .description(uploadRequestDto.description())
                .isShared(uploadRequestDto.isShared() != null ? uploadRequestDto.isShared() : false)
                .creatorId(memberId)
                .link(null) // link는 null로 저장
                .thumbnailUrl(imageUrl)
                .createdAt(null) // createdAt은 null로 저장
                .isVectorDbTrained(false)
                .build();

        Model3D savedModel = model3DRepository.save(model3D);
        log.info("📝 3D 모델 임시 저장: model3dId={}, name={}, furnitureType={}", 
            savedModel.getId(), savedModel.getName(), savedModel.getFurnitureType());

        // RabbitMQ로 메시지 전송 (furniture_type, is_shared 포함)
        model3DProducer.sendModel3DUploadMessage(imageUrl, memberId, savedModel.getId(), 
            uploadRequestDto.furnitureType(), uploadRequestDto.isShared());
        
        return imageUrl;
    }

    public void saveGeneratedModel(Model3DGenerationResponse response) {
        log.info("💾 3D 모델 DB 업데이트 시작: model3dId={}, modelUrl={}", 
            response.getModel3dId(), response.getModel3dUrl());

        try {
            // model3dId로 Model3D 찾기
            Model3D model3D = model3DRepository.findById(response.getModel3dId())
                    .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + response.getModel3dId() + "을 찾을 수 없습니다."));

            // link, createdAt, isVectorDbTrained만 업데이트
            model3D.updateGeneratedModel(
                    response.getModel3dUrl(), // link
                    LocalDateTime.now(), // createdAt
                    true // isVectorDbTrained
            );

            // 상태를 SUCCESS로 업데이트
            model3D.updateStatus("SUCCESS");

            Model3D updatedModel = model3DRepository.save(model3D);
            
            log.info("🖼️ 썸네일 URL 저장: {}", response.getThumbnailUrl());
            
            log.info("✅ 3D 모델 DB 업데이트 성공: model3DId={}, status={}", 
                updatedModel.getId(), updatedModel.getStatus());
            
        } catch (Exception e) {
            log.error("❌ 3D 모델 DB 업데이트 실패: model3dId={}, error={}", 
                response.getModel3dId(), e.getMessage(), e);
            throw new RuntimeException("3D 모델 업데이트 중 오류가 발생했습니다.", e);
        }
    }

    public void handleGenerationFailure(Model3DGenerationResponse response) {
        log.error("💥 3D 모델 생성 실패 처리: model3dId={}, memberId={}, message={}", 
            response.getModel3dId(), response.getMemberId(), response.getMessage());

        try {
            // model3dId로 Model3D 찾기
            Model3D model3D = model3DRepository.findById(response.getModel3dId())
                    .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + response.getModel3dId() + "을 찾을 수 없습니다."));

            // 상태를 FAILED로 업데이트하고 에러 메시지 저장
            model3D.updateStatus("FAILED");
            model3D.updateErrorMessage(response.getMessage());
            
            Model3D failedModel = model3DRepository.save(model3D);
            
            log.warn("⚠️ 실패 원인: {}", response.getMessage());
            log.warn("⚠️ 원본 이미지: {}", response.getOriginalImageUrl());
            log.info("❌ 3D 모델 상태 업데이트 완료: model3dId={}, status={}", 
                failedModel.getId(), failedModel.getStatus());
            
            
        } catch (Exception e) {
            log.error("❌ 실패 처리 중 추가 오류 발생: model3dId={}, error={}", 
                response.getModel3dId(), e.getMessage(), e);
        }
    }

    public List<Model3DResponseDto> getAllModel3Ds(Long memberId) {
        List<Model3D> model3Ds = model3DRepository.findAll();
        return model3Ds.stream()
                .map(Model3DResponseDto::from)
                .toList();
    }

    public Page<Model3DResponseDto> getModel3DsByMemberId(Long targetMemberId, Long memberId, String name, Pageable pageable) {
        Page<Model3D> model3Ds;
        if (name != null && !name.isEmpty()) {
            model3Ds = model3DRepository.findByCreatorIdAndNameContaining(targetMemberId, name, pageable);
        } else {
            model3Ds = model3DRepository.findByCreatorId(targetMemberId, pageable);
        }
        return model3Ds.map(Model3DResponseDto::from);
    }

    public Page<Model3DResponseDto> getSharedModel3Ds(Long memberId, String name, Pageable pageable) {
        Page<Model3D> model3Ds;

        if (name != null && !name.isEmpty()) {
            model3Ds = model3DRepository.findByIsSharedTrueAndNameContainingAndStatus(name, "SUCCESS", pageable);
        } else {
            model3Ds = model3DRepository.findByIsSharedTrueAndStatus("SUCCESS", pageable);
        }
        return model3Ds.map(Model3DResponseDto::from);
    }

    public List<Model3DResponseDto> getNotVectorDbTrainedModel3Ds() {
        List<Model3D> model3Ds = model3DRepository.findByIsVectorDbTrainedFalse();
        return model3Ds.stream()
                .map(Model3DResponseDto::from)
                .toList();
    }

    public void deleteAllModel3Ds(Long memberId) {
        List<Model3D> model3Ds = model3DRepository.findAll();
        List<Model3D> userModel3Ds = model3Ds.stream()
                .filter(model3D -> model3D.getCreatorId().equals(memberId))
                .toList();
        
        if (userModel3Ds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 3D 모델이 없습니다.");
        }

        // VectorDB에 학습된 모델들의 ID 추출하여 삭제 메시지 발송
        List<Long> trainedModel3dIds = userModel3Ds.stream()
                .filter(Model3D::getIsVectorDbTrained)
                .map(Model3D::getId)
                .toList();
        
        if (!trainedModel3dIds.isEmpty()) {
            model3DProducer.sendDeleteMessage(trainedModel3dIds, memberId);
            log.info("🗑️ VectorDB 일괄 삭제 요청: model3dIds={}", trainedModel3dIds);
        }
        
        model3DRepository.deleteAll(userModel3Ds);
    }
    
    private boolean isOwner(Long modelCreatorId, Long memberId) {
        return modelCreatorId.equals(memberId);
    }
}
