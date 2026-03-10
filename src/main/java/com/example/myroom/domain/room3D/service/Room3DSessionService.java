package com.example.myroom.domain.room3D.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;
import com.example.myroom.domain.room3D.dto.request.Room3DSessionCreateRequestDto;
import com.example.myroom.domain.room3D.dto.request.Room3DSessionUpdateInfoRequestDto;
import com.example.myroom.domain.room3D.dto.response.Room3DSessionResponseDto;
import com.example.myroom.domain.room3D.model.Room3DAsset;
import com.example.myroom.domain.room3D.model.Room3DSession;
import com.example.myroom.domain.room3D.repository.Room3DAssetRepository;
import com.example.myroom.domain.room3D.repository.Room3DSessionRepository;
import com.example.myroom.global.service.S3Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Room3DSessionService {

    private final Room3DSessionRepository sessionRepository;
    private final Room3DAssetRepository assetRepository;
    private final MemberRepository memberRepository;
    private final Model3DRepository model3DRepository;
    private final S3Service s3Service;

    @Transactional
    public Room3DSessionResponseDto createSession(
            Long memberId, 
            Room3DSessionCreateRequestDto requestDto, 
            MultipartFile xmlFile) {
        
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. id=" + memberId));

        // 에셋 조회
        Room3DAsset asset = assetRepository.findById(requestDto.assetId())
                .orElseThrow(() -> new EntityNotFoundException("에셋을 찾을 수 없습니다. id=" + requestDto.assetId()));

        // 3D 모델 조회
        List<Model3D> model3Ds = new ArrayList<>();
        if (requestDto.model3dIds() != null && !requestDto.model3dIds().isEmpty()) {
            model3Ds = model3DRepository.findAllById(requestDto.model3dIds());
            if (model3Ds.size() != requestDto.model3dIds().size()) {
                throw new EntityNotFoundException("일부 3D 모델을 찾을 수 없습니다.");
            }
        }

        // XML 파일 S3 업로드
        String xmlFileUrl = null;
        if (xmlFile != null && !xmlFile.isEmpty()) {
            xmlFileUrl = s3Service.uploadXmlFile(xmlFile, "room3d-sessions/");
        }

        // 세션 생성
        Room3DSession session = Room3DSession.builder()
                .asset(asset)
                .member(member)
                .xmlFileUrl(xmlFileUrl)
                .sessionName(requestDto.sessionName())
                .sessionDescription(requestDto.sessionDescription())
                .isShared(requestDto.isShared())
                .model3Ds(model3Ds)
                .build();

        Room3DSession savedSession = sessionRepository.save(session);
        return Room3DSessionResponseDto.from(savedSession);
    }

    public Room3DSessionResponseDto getSessionById(Long sessionId) {
        Room3DSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("세션을 찾을 수 없습니다. id=" + sessionId));
        return Room3DSessionResponseDto.from(session);
    }

    public Page<Room3DSessionResponseDto> getMySessionsPaginated(Long memberId, Pageable pageable) {
        return sessionRepository.findByMemberId(memberId, pageable)
                .map(Room3DSessionResponseDto::from);
    }

    @Transactional
    public Room3DSessionResponseDto updateSessionXml(Long sessionId, Long memberId, MultipartFile xmlFile) {
        Room3DSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("세션을 찾을 수 없습니다. id=" + sessionId));

        // 권한 확인
        if (!session.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("세션을 수정할 권한이 없습니다.");
        }

        if (xmlFile == null || xmlFile.isEmpty()) {
            throw new IllegalArgumentException("XML 파일이 필요합니다.");
        }

        // 기존 파일 삭제 (옵션)
        if (session.getXmlFileUrl() != null) {
            s3Service.deleteFile(session.getXmlFileUrl());
        }

        // 새 파일 업로드
        String newXmlFileUrl = s3Service.uploadXmlFile(xmlFile, "room3d-sessions/");
        session.updateXmlFile(newXmlFileUrl);

        return Room3DSessionResponseDto.from(session);
    }

    @Transactional
    public Room3DSessionResponseDto updateSessionInfo(
            Long sessionId, 
            Long memberId, 
            Room3DSessionUpdateInfoRequestDto requestDto) {
        
        Room3DSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("세션을 찾을 수 없습니다. id=" + sessionId));

        // 권한 확인
        if (!session.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("세션을 수정할 권한이 없습니다.");
        }

        // 3D 모델 조회 (변경된 경우)
        List<Model3D> model3Ds = null;
        if (requestDto.model3dIds() != null) {
            model3Ds = model3DRepository.findAllById(requestDto.model3dIds());
            if (model3Ds.size() != requestDto.model3dIds().size()) {
                throw new EntityNotFoundException("일부 3D 모델을 찾을 수 없습니다.");
            }
        }

        // 세션 정보 업데이트
        session.updateSessionInfo(
                requestDto.sessionName(),
                requestDto.sessionDescription(),
                requestDto.isShared(),
                model3Ds
        );

        return Room3DSessionResponseDto.from(session);
    }
}
