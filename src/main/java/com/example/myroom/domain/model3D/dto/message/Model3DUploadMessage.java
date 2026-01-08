package com.example.myroom.domain.model3D.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 3D 모델 파일 업로드 메시지
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model3DUploadMessage {
    private String imageUrl;
    private Long memberId;
    private long timestamp;
}

