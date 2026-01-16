package com.example.myroom.domain.model3D.dto.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 3D 모델 생성 완료 응답 메시지
 * - 3D 모델 생성 서버에서 모델 생성이 완료되면 이 형식으로 메시지를 보냅니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model3DGenerationResponse {
    
    /**
     * 3D 모델 ID
     */
    @JsonProperty("model_3d_id")
    @JsonAlias({"modelId", "model_id", "id", "model3dId"})
    private Long model3dId;
    
    /**
     * 요청한 회원 ID
     */
    @JsonProperty("member_id")
    @JsonAlias("memberId")
    private Long memberId;
    
    /**
     * 원본 이미지 URL (생성 요청 시 전송했던 이미지)
     */
    @JsonProperty("original_image_url")
    @JsonAlias("originalImageUrl")
    private String originalImageUrl;
    
    /**
     * 생성된 3D 모델 파일 URL (GLB, GLTF 등)
     */
    @JsonProperty("model_3d_url")
    @JsonAlias("model3dUrl")
    private String model3dUrl;
    
    /**
     * 썸네일 이미지 URL (선택사항)
     */
    @JsonProperty("thumbnail_url")
    @JsonAlias("thumbnailUrl")
    private String thumbnailUrl;
    
    /**
     * 생성 상태 (SUCCESS, FAILED, PROCESSING 등)
     */
    private String status;
    
    /**
     * 상태 메시지 또는 에러 메시지
     */
    private String message;
    
    /**
     * 생성 완료 시각 (Unix timestamp, milliseconds)
     */
    private Long timestamp;
    
    /**
     * 생성에 걸린 시간 (초 단위)
     */
    @JsonProperty("processing_time_seconds")
    @JsonAlias("processingTimeSeconds")
    private Integer processingTimeSeconds;
}
