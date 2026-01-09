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
import com.example.myroom.domain.model3D.dto.message.Model3DGenerationResponse;
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

    /**
     * 3D 모델 생성 완료 후 DB 저장
     * - 3D 모델 생성 서버에서 성공적으로 모델을 생성한 경우 호출됩니다.
     * 
     * @param response 3D 모델 생성 완료 응답 메시지
     */
    public void saveGeneratedModel(Model3DGenerationResponse response) {
        log.info("💾 3D 모델 DB 저장 시작: memberId={}, modelUrl={}", 
            response.getMemberId(), response.getModel3dUrl());

        try {
            // 임의 로직: 생성된 3D 모델 정보를 DB에 저장
            Model3D model3D = Model3D.builder()
                    .createdAt(LocalDateTime.now())
                    .link(response.getModel3dUrl()) // 생성된 3D 모델 URL
                    .creatorId(response.getMemberId()) // 요청한 회원 ID
                    .isShared(false) // 기본값: 비공개
                    .description("AI 생성 3D 모델 - " + LocalDateTime.now()) // 자동 생성 설명
                    .build();

            Model3D savedModel = model3DRepository.save(model3D);
            
            log.info("✅ 3D 모델 DB 저장 성공: model3DId={}, creatorId={}", 
                savedModel.getId(), savedModel.getCreatorId());
            
            // 추가 로직 예시:
            // 1. 회원에게 푸시 알림 전송 (생성 완료 알림)
            // 2. 썸네일 이미지 별도 처리
            // 3. 통계 정보 업데이트 (생성 횟수 등)
            // 4. 캐시 갱신
            
        } catch (Exception e) {
            log.error("❌ 3D 모델 DB 저장 실패: memberId={}, error={}", 
                response.getMemberId(), e.getMessage(), e);
            throw new RuntimeException("3D 모델 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 3D 모델 생성 실패 처리
     * - 3D 모델 생성 서버에서 실패한 경우 호출됩니다.
     * 
     * @param response 3D 모델 생성 실패 응답 메시지
     */
    public void handleGenerationFailure(Model3DGenerationResponse response) {
        log.error("💥 3D 모델 생성 실패 처리: memberId={}, message={}", 
            response.getMemberId(), response.getMessage());

        try {
            // 임의 로직: 실패 내역 기록 및 처리
            // 1. 실패 로그를 별도 테이블에 저장 (향후 분석용)
            // 2. 회원에게 실패 알림 전송
            // 3. 관리자에게 에러 리포트 전송
            // 4. 재시도 큐에 추가 (자동 재시도 옵션)
            
            log.warn("⚠️ 실패 원인: {}", response.getMessage());
            log.warn("⚠️ 원본 이미지: {}", response.getOriginalImageUrl());
            
            // 실제 운영 환경에서는:
            // - 에러 유형에 따라 다른 처리 (이미지 품질 문제, 서버 오류 등)
            // - 실패 횟수 추적 및 임계값 설정
            // - 자동 환불 처리 (유료 서비스인 경우)
            
        } catch (Exception e) {
            log.error("❌ 실패 처리 중 추가 오류 발생: {}", e.getMessage(), e);
        }
    }
}
