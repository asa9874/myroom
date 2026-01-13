package com.example.myroom.domain.recommand.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.recommand.service.RecommandService;
import com.example.myroom.global.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 가구 추천 API 컨트롤러
 * - 이미지 업로드 및 가구 추천 요청
 * - 추천 결과는 WebSocket을 통해 실시간으로 전송됨
 */
@Slf4j
@RestController
@RequestMapping("/api/recommand")
@RequiredArgsConstructor
public class RecommandController {
    
    private final RecommandService recommandService;
    
    /**
     * 가구 추천 요청
     * - 이미지를 업로드하고 가구 추천을 요청합니다.
     * - 추천 요청은 RabbitMQ 메시지 큐에 발송됩니다.
     * - 결과는 WebSocket을 통해 실시간으로 전송됩니다.
     * 
     * @param imageFile 분석할 이미지 파일
     * @param category 추천할 가구 카테고리 (선택사항, 기본값: 'chair')
     *        - 예: 'chair', 'table', 'lamp', 'sofa', 'desk', 'shelf' 등
     * @param topK 반환할 추천 결과 개수 (선택사항, 기본값: 5)
     * @param member 인증된 사용자 정보
     * @return 처리 상태 메시지
     */
    @PostMapping(value = "/request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> requestRecommandation(
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @RequestParam(value = "category", required = false, defaultValue = "chair") String category,
            @RequestParam(value = "topK", required = false, defaultValue = "5") Integer topK,
            @AuthenticationPrincipal CustomUserDetails member) {
        
        log.info("추천 요청 수신: memberId={}, category={}, topK={}, fileName={}", 
                member.getId(), category, topK, imageFile.getOriginalFilename());
        
        // 입력 값 검증
        if (imageFile.isEmpty()) {
            log.warn("❌ 빈 파일 업로드 시도: memberId={}", member.getId());
            return ResponseEntity.badRequest().body("이미지 파일을 선택해주세요.");
        }

        if (!imageFile.getContentType().startsWith("image/")) {
            log.warn("❌ 이미지가 아닌 파일 업로드 시도: memberId={}, contentType={}", 
                    member.getId(), imageFile.getContentType());
            return ResponseEntity.badRequest().body("이미지 파일만 업로드 가능합니다.");
        }

        if (topK <= 0 || topK > 100) {
            log.warn("⚠️ 유효하지 않은 topK 값: memberId={}, topK={}", member.getId(), topK);
            topK = 5; // 기본값으로 설정
        }

        try {
            log.info("✅ 추천 요청 처리 시작: memberId={}", member.getId());
            
            // 추천 서비스 호출
            String message = recommandService.requestRecommandation(
                    imageFile, 
                    category, 
                    topK, 
                    member.getId()
            );
            
            log.info("✅ 추천 요청 처리 완료: memberId={}", member.getId());
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            log.error("❌ 추천 요청 처리 중 오류: memberId={}, error={}", member.getId(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("추천 요청 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
