package com.example.myroom.domain.model3D.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.model3D.dto.message.ModelDimensionsImageResponseMessage;
import com.example.myroom.domain.model3D.dto.request.ModelDimensionsCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.ModelDimensionsUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.ModelDimensionsResponseDto;
import com.example.myroom.domain.model3D.messaging.Model3DProducer;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.model.ModelDimensions;
import com.example.myroom.domain.model3D.repository.ModelDimensionsRepository;
import com.example.myroom.domain.model3D.repository.Model3DRepository;
import com.example.myroom.global.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelDimensionsService {
    private final ModelDimensionsRepository modelDimensionsRepository;
    private final Model3DRepository model3DRepository;
        private final S3Service s3Service;
        private final Model3DProducer model3DProducer;

        @Transactional
        public String requestModelDimensionsByImage(Long model3dId, MultipartFile imageFile, Long memberId) {
                validateImageFile(imageFile);

                model3DRepository.findById(model3dId)
                                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));

                String imageUrl = s3Service.uploadFile(imageFile, "model3d/dimensions/images/");
                model3DProducer.sendModelDimensionsImageRequestMessage(model3dId, memberId, imageUrl);

                return "치수 분석 요청이 완료되었습니다. 결과는 WebSocket으로 전달됩니다.";
        }

    public ModelDimensionsResponseDto getModelDimensionsById(Long modelDimensionsId) {
        ModelDimensions modelDimensions = modelDimensionsRepository.findById(modelDimensionsId)
                .orElseThrow(() -> new IllegalArgumentException("치수 정보 " + modelDimensionsId + "을 찾을 수 없습니다."));
        return ModelDimensionsResponseDto.from(modelDimensions);
    }

    public ModelDimensionsResponseDto getModelDimensionsByModel3DId(Long model3dId) {
        ModelDimensions modelDimensions = modelDimensionsRepository.findByModel3DId(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "의 치수 정보를 찾을 수 없습니다."));
        return ModelDimensionsResponseDto.from(modelDimensions);
    }

    public List<ModelDimensionsResponseDto> getAllModelDimensions() {
        List<ModelDimensions> modelDimensionsList = modelDimensionsRepository.findAll();
        return modelDimensionsList.stream()
                .map(ModelDimensionsResponseDto::from)
                .toList();
    }

    public ModelDimensionsResponseDto createModelDimensions(ModelDimensionsCreateRequestDto createRequestDto) {
        Model3D model3D = model3DRepository.findById(createRequestDto.model3dId())
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + createRequestDto.model3dId() + "을 찾을 수 없습니다."));

        ModelDimensions modelDimensions = ModelDimensions.builder()
                .model3D(model3D)
                .width(createRequestDto.width())
                .length(createRequestDto.length())
                .height(createRequestDto.height())
                .build();

        ModelDimensions savedModelDimensions = modelDimensionsRepository.save(modelDimensions);
        return ModelDimensionsResponseDto.from(savedModelDimensions);
    }

    public ModelDimensionsResponseDto updateModelDimensions(Long modelDimensionsId, ModelDimensionsUpdateRequestDto updateRequestDto) {
        ModelDimensions modelDimensions = modelDimensionsRepository.findById(modelDimensionsId)
                .orElseThrow(() -> new IllegalArgumentException("치수 정보 " + modelDimensionsId + "을 찾을 수 없습니다."));

        modelDimensions.update(
                updateRequestDto.width(),
                updateRequestDto.length(),
                updateRequestDto.height());

        ModelDimensions updatedModelDimensions = modelDimensionsRepository.save(modelDimensions);
        return ModelDimensionsResponseDto.from(updatedModelDimensions);
    }

    public ModelDimensionsResponseDto updateModelDimensionsByModel3dId(Long model3dId, ModelDimensionsUpdateRequestDto updateRequestDto) {
        if (modelDimensionsRepository.existsByModel3DId(model3dId)) {
            // 기존 치수 정보가 있으면 업데이트
            ModelDimensions modelDimensions = modelDimensionsRepository.findByModel3DId(model3dId)
                    .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "의 치수 정보를 찾을 수 없습니다."));
            
            modelDimensions.update(
                    updateRequestDto.width(),
                    updateRequestDto.length(),
                    updateRequestDto.height());
            
            ModelDimensions updatedModelDimensions = modelDimensionsRepository.save(modelDimensions);
            return ModelDimensionsResponseDto.from(updatedModelDimensions);
        } else {
            // 치수 정보가 없으면 새로 생성
            Model3D model3D = model3DRepository.findById(model3dId)
                    .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + model3dId + "을 찾을 수 없습니다."));
            
            ModelDimensions modelDimensions = ModelDimensions.builder()
                    .model3D(model3D)
                    .width(updateRequestDto.width())
                    .length(updateRequestDto.length())
                    .height(updateRequestDto.height())
                    .build();
            
            ModelDimensions savedModelDimensions = modelDimensionsRepository.save(modelDimensions);
            return ModelDimensionsResponseDto.from(savedModelDimensions);
        }
    }

    public void deleteModelDimensions(Long modelDimensionsId) {
        modelDimensionsRepository.findById(modelDimensionsId)
                .orElseThrow(() -> new IllegalArgumentException("치수 정보 " + modelDimensionsId + "을 찾을 수 없습니다."));

        modelDimensionsRepository.deleteById(modelDimensionsId);
    }

        @Transactional
        public ModelDimensionsResponseDto upsertDimensionsFromAiResponse(ModelDimensionsImageResponseMessage response) {
                if (response.getModel3dId() == null) {
                        throw new IllegalArgumentException("치수 응답에 model3dId가 없습니다.");
                }
                if (response.getDimensions() == null) {
                        throw new IllegalArgumentException("치수 응답에 dimensions 값이 없습니다.");
                }

                Model3D model3D = model3DRepository.findById(response.getModel3dId())
                                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + response.getModel3dId() + "을 찾을 수 없습니다."));

                Float width = toRequiredFloat(response.getDimensions().getWidth(), "width");
                Float length = toRequiredFloat(response.getDimensions().getDepth(), "depth");
                Float height = toRequiredFloat(response.getDimensions().getHeight(), "height");

                ModelDimensions modelDimensions = modelDimensionsRepository.findByModel3DId(response.getModel3dId())
                                .orElseGet(() -> ModelDimensions.builder()
                                                .model3D(model3D)
                                                .build());

                modelDimensions.update(width, length, height);

                ModelDimensions saved = modelDimensionsRepository.save(modelDimensions);
                return ModelDimensionsResponseDto.from(saved);
        }

        private Float toRequiredFloat(Double value, String fieldName) {
                if (value == null) {
                        throw new IllegalArgumentException("치수 응답에 " + fieldName + " 값이 없습니다.");
                }
                return value.floatValue();
        }

        private void validateImageFile(MultipartFile imageFile) {
                if (imageFile == null || imageFile.isEmpty()) {
                        throw new IllegalArgumentException("치수 분석용 이미지 파일이 필요합니다.");
                }

                String contentType = imageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                        throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
                }
        }

}
