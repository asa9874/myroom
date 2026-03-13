package com.example.myroom.domain.model3D.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.image.ImageUploadService;
import com.example.myroom.domain.image.Model3DImageUrls;
import com.example.myroom.domain.image.S3ImageUploadService;
import com.example.myroom.domain.model3D.dto.message.Model3DGenerationResponse;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestV2Dto;
import com.example.myroom.domain.model3D.dto.request.Model3DUpdateRequestV3Dto;
import com.example.myroom.domain.model3D.dto.request.Model3DUploadRequestDto;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.domain.model3D.messaging.Model3DProducer;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper;

    public Model3DResponseDto getModel3DById(Long model3dId, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));
        if (!isOwner(model3D.getCreatorId(), memberId) && !model3D.getIsShared()) {
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
                null,
                updateRequestDto.shopPageLink());

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
                null,
                updateRequestDto.shopPageLink());

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

    public Model3DResponseDto updateModel3Dv3(Long model3dId, Model3DUpdateRequestV3Dto updateRequestDto, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

        if (!isOwner(model3D.getCreatorId(), memberId)) {
            throw new IllegalArgumentException("3D 모델을 수정할 권한이 없습니다.");
        }

        model3D.updateV3(
                updateRequestDto.name(),
                updateRequestDto.isShared(),
                updateRequestDto.description(),
                updateRequestDto.furnitureType(),
                updateRequestDto.shopPageLink());

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
        Model3DImageUrls imageUrls;
        try { 
            //imageUrls = imageUploadService.uploadModel3DImages(file); //로컬저장
            imageUrls = s3ImageUploadService.uploadModel3DImages(file); // S3저장
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
                .thumbnailUrl(imageUrls.thumbnailUrl())
                .trainingImageUrl(imageUrls.trainingImageUrl())
                .trainingImageUrls(serializeImageUrls(List.of(imageUrls.trainingImageUrl())))
                .createdAt(LocalDateTime.now())
                .isVectorDbTrained(false)
                .build();

        Model3D savedModel = model3DRepository.save(model3D);
        log.info("📝 3D 모델 임시 저장: model3dId={}, name={}, furnitureType={}", 
            savedModel.getId(), savedModel.getName(), savedModel.getFurnitureType());

        // RabbitMQ로 메시지 전송 (학습용 1024x1024 이미지 URL 사용)
        model3DProducer.sendModel3DUploadMessage(imageUrls.trainingImageUrl(), memberId, savedModel.getId(), 
            uploadRequestDto.furnitureType(), uploadRequestDto.isShared());
        
        return imageUrls.thumbnailUrl();
    }

    public String uploadModel3DMultiFile(List<MultipartFile> files, Model3DUploadRequestDto uploadRequestDto, Long memberId) {
        validateMultiUploadFiles(files);

        List<String> trainingImageUrls = new ArrayList<>();
        String thumbnailUrl;

        try {
            Model3DImageUrls firstImageUrls = s3ImageUploadService.uploadModel3DImages(files.get(0));
            thumbnailUrl = firstImageUrls.thumbnailUrl();
            trainingImageUrls.add(firstImageUrls.trainingImageUrl());

            for (int i = 1; i < files.size(); i++) {
                Model3DImageUrls imageUrls = s3ImageUploadService.uploadModel3DImages(files.get(i));
                trainingImageUrls.add(imageUrls.trainingImageUrl());
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        Model3D model3D = Model3D.builder()
                .name(uploadRequestDto.name())
                .furnitureType(uploadRequestDto.furnitureType())
                .description(uploadRequestDto.description())
                .isShared(uploadRequestDto.isShared() != null ? uploadRequestDto.isShared() : false)
                .creatorId(memberId)
                .link(null)
                .thumbnailUrl(thumbnailUrl)
                .trainingImageUrl(trainingImageUrls.get(0))
                .trainingImageUrls(serializeImageUrls(trainingImageUrls))
                .createdAt(LocalDateTime.now())
                .isVectorDbTrained(false)
                .build();

        Model3D savedModel = model3DRepository.save(model3D);
        log.info("📝 3D 멀티뷰 모델 임시 저장: model3dId={}, name={}, furnitureType={}, imageCount={}",
                savedModel.getId(), savedModel.getName(), savedModel.getFurnitureType(), trainingImageUrls.size());

        model3DProducer.sendModel3DMultiUploadMessage(trainingImageUrls, memberId, savedModel.getId(),
                uploadRequestDto.furnitureType(), uploadRequestDto.isShared());

        return thumbnailUrl;
    }

    private void validateMultiUploadFiles(List<MultipartFile> files) {
        if (files == null || files.size() < 2) {
            throw new IllegalArgumentException("멀티뷰 3D 생성에는 최소 2장의 이미지가 필요합니다.");
        }
        if (files.size() > 4) {
            throw new IllegalArgumentException("멀티뷰 3D 생성은 최대 4장까지 업로드할 수 있습니다.");
        }
        if (files.get(0) == null || files.get(0).isEmpty() || files.get(1) == null || files.get(1).isEmpty()) {
            throw new IllegalArgumentException("첫 번째와 두 번째 이미지는 필수입니다.");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("업로드 이미지에 빈 파일이 포함되어 있습니다.");
            }
        }
    }

    private String serializeImageUrls(List<String> imageUrls) {
        try {
            return objectMapper.writeValueAsString(imageUrls);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("학습 이미지 URL 저장 중 오류가 발생했습니다.", e);
        }
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
