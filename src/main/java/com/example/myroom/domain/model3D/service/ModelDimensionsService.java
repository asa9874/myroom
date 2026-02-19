package com.example.myroom.domain.model3D.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myroom.domain.model3D.dto.request.ModelDimensionsCreateRequestDto;
import com.example.myroom.domain.model3D.dto.request.ModelDimensionsUpdateRequestDto;
import com.example.myroom.domain.model3D.dto.response.ModelDimensionsResponseDto;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.model.ModelDimensions;
import com.example.myroom.domain.model3D.repository.ModelDimensionsRepository;
import com.example.myroom.domain.model3D.repository.Model3DRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelDimensionsService {
    private final ModelDimensionsRepository modelDimensionsRepository;
    private final Model3DRepository model3DRepository;

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

}
