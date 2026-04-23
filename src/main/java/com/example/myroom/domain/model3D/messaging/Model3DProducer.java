package com.example.myroom.domain.model3D.messaging;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.myroom.domain.model3D.dto.message.Model3DDeleteMessage;
import com.example.myroom.domain.model3D.dto.message.ModelDimensionsImageRequestMessage;
import com.example.myroom.domain.model3D.dto.message.Model3DMetadataUpdateMessage;
import com.example.myroom.domain.model3D.dto.message.Model3DUploadMessage;
import com.example.myroom.domain.model3D.model.FurnitureCategory;
import com.example.myroom.global.config.RabbitConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 3D 모델 메시지 발송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Model3DProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 3D 모델 업로드 메시지 발송
     */
    public void sendModel3DUploadMessage(String trainingImageUrl, Long memberId, Long model3dId, FurnitureCategory furnitureType, Boolean isShared) {
        Model3DUploadMessage message = Model3DUploadMessage.builder()
                .imageUrl(trainingImageUrl)
                .memberId(memberId)
                .model3dId(model3dId)
                .furnitureType(furnitureType)
                .isShared(isShared)
                .timestamp(System.currentTimeMillis())
                .build();

        log.info("3D 모델 업로드 메시지 발송: imageUrl={}, memberId={}, model3dId={}, furnitureType={}, isShared={}", 
            trainingImageUrl, memberId, model3dId, furnitureType, isShared);

        rabbitTemplate.convertAndSend(
                RabbitConfig.MODEL3D_EXCHANGE, // 어느 교환기(우체국)로 보낼지
                RabbitConfig.MODEL3D_ROUTING_KEY, // 어떤 주소(라우팅 키)로 보낼지
                message // 보낼 내용(메시지 객체)
        );
    }

            public void sendModel3DMultiUploadMessage(List<String> trainingImageUrls, Long memberId, Long model3dId,
                FurnitureCategory furnitureType, Boolean isShared) {
            Model3DUploadMessage message = Model3DUploadMessage.builder()
                .imageUrls(trainingImageUrls)
                .memberId(memberId)
                .model3dId(model3dId)
                .furnitureType(furnitureType)
                .isShared(isShared)
                .timestamp(System.currentTimeMillis())
                .build();

            log.info("3D 멀티뷰 업로드 메시지 발송: imageUrlsCount={}, memberId={}, model3dId={}, furnitureType={}, isShared={}",
                trainingImageUrls.size(), memberId, model3dId, furnitureType, isShared);

            rabbitTemplate.convertAndSend(
                RabbitConfig.MODEL3D_EXCHANGE,
                RabbitConfig.MODEL3D_ROUTING_KEY,
                message
            );
            }

    /**
     * VectorDB 메타데이터 업데이트 메시지 발송
     * - 3D 모델 정보가 수정되면 VectorDB의 메타데이터도 함께 업데이트해야 합니다.
     */
    public void sendMetadataUpdateMessage(Long model3dId, Long memberId, String name, String description, Boolean isShared) {
        Model3DMetadataUpdateMessage message = Model3DMetadataUpdateMessage.builder()
                .model3dId(model3dId)
                .memberId(memberId)
                .name(name)
                .description(description)
                .isShared(isShared)
                .timestamp(System.currentTimeMillis())
                .build();

        log.info("📤 VectorDB 메타데이터 업데이트 메시지 발송: model3dId={}, memberId={}, name={}, description={}, isShared={}", 
            model3dId, memberId, name, description, isShared);

        rabbitTemplate.convertAndSend(
                RabbitConfig.MODEL3D_EXCHANGE,
                RabbitConfig.MODEL3D_METADATA_UPDATE_ROUTING_KEY,
                message
        );
    }

    /**
     * VectorDB 삭제 메시지 발송
     * - 3D 모델이 삭제되면 VectorDB에서도 해당 데이터를 삭제해야 합니다.
     */
    public void sendDeleteMessage(List<Long> model3dIds, Long memberId) {
        Model3DDeleteMessage message = Model3DDeleteMessage.builder()
                .model3dIds(model3dIds)
                .memberId(memberId)
                .timestamp(System.currentTimeMillis())
                .build();

        log.info("🗑️ VectorDB 삭제 메시지 발송: model3dIds={}, memberId={}", model3dIds, memberId);

        rabbitTemplate.convertAndSend(
                RabbitConfig.MODEL3D_EXCHANGE,
                RabbitConfig.MODEL3D_DELETE_ROUTING_KEY,
                message
        );
    }

            /**
             * 치수 이미지 분석 요청 메시지 발송
             */
            public void sendModelDimensionsImageRequestMessage(Long model3dId, Long memberId, String imageUrl) {
            ModelDimensionsImageRequestMessage message = ModelDimensionsImageRequestMessage.builder()
                .model3dId(model3dId)
                .memberId(memberId)
                .imageUrl(imageUrl)
                .timestamp(System.currentTimeMillis())
                .build();

            log.info("📏 치수 이미지 분석 요청 메시지 발송: model3dId={}, memberId={}, imageUrl={}",
                model3dId, memberId, imageUrl);

            rabbitTemplate.convertAndSend(
                RabbitConfig.MODEL3D_EXCHANGE,
                RabbitConfig.MODEL3D_DIMENSIONS_REQUEST_ROUTING_KEY,
                message
            );
            }
}
