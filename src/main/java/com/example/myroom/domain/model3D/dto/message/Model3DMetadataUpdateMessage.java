package com.example.myroom.domain.model3D.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * VectorDB 메타데이터 업데이트를 위한 메시지 DTO
 * - 3D 모델 정보가 수정되면 VectorDB의 메타데이터도 함께 업데이트해야 합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model3DMetadataUpdateMessage {
    
    @JsonProperty("model3d_id")
    private Long model3dId;
    
    @JsonProperty("member_id")
    private Long memberId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("is_shared")
    private Boolean isShared;
    
    @JsonProperty("timestamp")
    private Long timestamp;
}
