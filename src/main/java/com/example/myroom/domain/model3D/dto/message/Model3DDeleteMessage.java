package com.example.myroom.domain.model3D.dto.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * VectorDB에서 3D 모델 삭제를 위한 메시지 DTO
 * - 3D 모델이 삭제되면 VectorDB에서도 해당 데이터를 삭제해야 합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model3DDeleteMessage {
    
    @JsonProperty("model3d_ids")
    private List<Long> model3dIds;
    
    @JsonProperty("member_id")
    private Long memberId;
    
    @JsonProperty("timestamp")
    private Long timestamp;
}
