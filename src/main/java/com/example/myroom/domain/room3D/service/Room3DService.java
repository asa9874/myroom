package com.example.myroom.domain.room3D.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.room3D.dto.message.Room3DResponseMessage;
import com.example.myroom.domain.room3D.dto.request.Room3DCreateRequestDto;
import com.example.myroom.domain.room3D.dto.request.Room3DUpdateRequestDto;
import com.example.myroom.domain.room3D.dto.response.Room3DResponseDto;
import com.example.myroom.domain.room3D.messaging.Room3DProducer;
import com.example.myroom.domain.room3D.model.Room3D;
import com.example.myroom.domain.room3D.repository.Room3DRepository;
import com.example.myroom.global.service.S3Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Room3DService {

    private final Room3DRepository room3DRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final Room3DProducer room3DProducer;

    @Transactional
    public Room3DResponseDto createRoom3D(
            MultipartFile imageFile,
            Room3DCreateRequestDto requestDto,
            Long memberId) {
        validateImageFile(imageFile);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "를 찾을 수 없습니다."));

        String drawingImageUrl = s3Service.uploadFile(imageFile, "room3d/images/");

        Room3D room3D = Room3D.builder()
                .member(member)
                .roomName(requestDto.roomName())
                .description(requestDto.description())
                .drawingImageUrl(drawingImageUrl)
                .drawingXmlUrl(null)
                .success(null)
                .build();

        Room3D savedRoom3D = room3DRepository.save(room3D);

        room3DProducer.sendRoom3DRequestMessage(
                savedRoom3D.getId(),
                memberId,
                savedRoom3D.getDrawingImageUrl(),
                savedRoom3D.getRoomName(),
                savedRoom3D.getDescription());

        return Room3DResponseDto.from(savedRoom3D);
    }

    @Transactional
    public Room3DResponseDto updateRoom3D(
            Long room3dId,
            Room3DUpdateRequestDto requestDto,
            MultipartFile xmlFile,
            Long memberId) {
        Room3D room3D = getOwnedRoom3D(room3dId, memberId);

        room3D.updateInfo(requestDto.roomName(), requestDto.description());

        if (xmlFile != null && !xmlFile.isEmpty()) {
            validateXmlFile(xmlFile);

            String oldXmlFileUrl = room3D.getDrawingXmlUrl();
            String newXmlFileUrl = s3Service.uploadXmlFile(xmlFile, "room3d/xml/");
            room3D.updateXmlFileUrl(newXmlFileUrl);

            if (oldXmlFileUrl != null && !oldXmlFileUrl.isBlank()) {
                s3Service.deleteFile(oldXmlFileUrl);
            }
        }

        Room3D updatedRoom3D = room3DRepository.save(room3D);
        return Room3DResponseDto.from(updatedRoom3D);
    }

    public Room3DResponseDto getRoom3DById(Long room3dId, Long memberId) {
        Room3D room3D = getOwnedRoom3D(room3dId, memberId);
        return Room3DResponseDto.from(room3D);
    }

    public Page<Room3DResponseDto> getMyRoom3Ds(Long memberId, Pageable pageable) {
        return room3DRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(Room3DResponseDto::from);
    }

    @Transactional
    public void deleteRoom3D(Long room3dId, Long memberId) {
        Room3D room3D = getOwnedRoom3D(room3dId, memberId);

        s3Service.deleteFile(room3D.getDrawingImageUrl());
        s3Service.deleteFile(room3D.getDrawingXmlUrl());

        room3DRepository.delete(room3D);
    }

    @Transactional
    public void handleRoom3DResponse(Room3DResponseMessage response) {
        Room3D room3D = room3DRepository.findById(response.getRoom3dId())
                .orElseThrow(() -> new EntityNotFoundException("Room3D를 찾을 수 없습니다. id=" + response.getRoom3dId()));

        if (!room3D.getMember().getId().equals(response.getMemberId())) {
            throw new IllegalArgumentException("Room3D 응답의 회원 정보가 일치하지 않습니다.");
        }

        if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
            if (response.getXmlFileUrl() == null || response.getXmlFileUrl().isBlank()) {
                throw new IllegalArgumentException("SUCCESS 응답에는 xmlFileUrl이 필요합니다.");
            }
            room3D.updateAiResult(true, response.getXmlFileUrl());
        } else if ("FAILED".equalsIgnoreCase(response.getStatus())) {
            room3D.updateAiResult(false, null);
        } else {
            log.warn("지원하지 않는 Room3D 상태값입니다. status={}", response.getStatus());
            return;
        }

        room3DRepository.save(room3D);
    }

    private Room3D getOwnedRoom3D(Long room3dId, Long memberId) {
        return room3DRepository.findByIdAndMemberId(room3dId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("Room3D를 찾을 수 없습니다. id=" + room3dId));
    }

    private void validateImageFile(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("도면 이미지는 필수입니다.");
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }

    private void validateXmlFile(MultipartFile xmlFile) {
        String originalFilename = xmlFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException("XML 파일만 업로드 가능합니다.");
        }
    }
}
