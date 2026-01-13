package com.example.myroom.domain.recommand.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 추천 요청 메시지
 * - 이미지 URL을 포함하여 메시지 큐에 발송됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommandRequestMessage {
    
    /**
     * 요청한 회원 ID
     */
    private Long memberId;
    
    /**
     * 분석할 이미지 URL
     */
    private String imageUrl;
    
    /**
     * 추천할 가구 카테고리 (선택사항, 기본값: 'chair')
     * - 예: 'chair', 'table', 'lamp', 'sofa' 등
     */
    private String category;
    
    /**
     * 반환할 추천 결과 개수 (선택사항, 기본값: 5)
     * - 최대 개수는 별도로 제한할 수 있음
     */
    private Integer topK;
    
    /**
     * 요청 시각 (Unix timestamp, milliseconds)
     */
    private Long timestamp;
}
