package com.example.myroom.fake.service;

import java.time.Instant;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.fake.dto.request.FakeRoomCreateRequestDto;
import com.example.myroom.fake.dto.request.FakeRoomUpdateRequestDto;
import com.example.myroom.fake.dto.response.FakeRoomResponseDto;
import com.example.myroom.fake.model.FakeRoom;
import com.example.myroom.fake.repository.FakeRoomRepository;
import com.example.myroom.domain.room3D.dto.message.Room3DResponseMessage;
import com.example.myroom.domain.socket.service.WebSocketNotificationService;
import com.example.myroom.global.service.S3Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
@Transactional(readOnly = true)
public class FakeRoomService {

    private final FakeRoomRepository fakeRoomRepository;
    private final S3Service s3Service;
    private final WebSocketNotificationService webSocketNotificationService;
    private final TaskScheduler taskScheduler;

    public FakeRoomService(
            FakeRoomRepository fakeRoomRepository,
            S3Service s3Service,
            WebSocketNotificationService webSocketNotificationService,
            @Qualifier("fakeRoomTaskScheduler") TaskScheduler taskScheduler) {
        this.fakeRoomRepository = fakeRoomRepository;
        this.s3Service = s3Service;
        this.webSocketNotificationService = webSocketNotificationService;
        this.taskScheduler = taskScheduler;
    }

    @Transactional
    public FakeRoomResponseDto createFakeRoom(
            MultipartFile imageFile,
            MultipartFile xmlFile,
            FakeRoomCreateRequestDto requestDto) {
        validateImageFile(imageFile);
        validateXmlFile(xmlFile);

        String drawingImageUrl = s3Service.uploadFile(imageFile, "fake-room/images/");
        String xmlFileUrl = s3Service.uploadXmlFile(xmlFile, "fake-room/xml/");

        FakeRoom fakeRoom = FakeRoom.builder()
                .roomName(requestDto.roomName())
                .description(requestDto.description())
            .drawingImageUrl(drawingImageUrl)
                .xmlFileUrl(xmlFileUrl)
                .build();

        FakeRoom savedFakeRoom = fakeRoomRepository.save(fakeRoom);
        return FakeRoomResponseDto.from(savedFakeRoom);
    }

    public FakeRoomResponseDto getFakeRoomById(Long fakeRoomId) {
        FakeRoom fakeRoom = fakeRoomRepository.findById(fakeRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Fake room not found. id=" + fakeRoomId));
        return FakeRoomResponseDto.from(fakeRoom);
    }

    public void postFakeRoom(Long fakeRoomId) {
        FakeRoom fakeRoom = fakeRoomRepository.findById(fakeRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Fake room not found. id=" + fakeRoomId));

        scheduleRoom3DNotification(fakeRoom);
    }

    @Transactional
    public FakeRoomResponseDto updateFakeRoom(
            Long fakeRoomId,
            FakeRoomUpdateRequestDto requestDto,
            MultipartFile imageFile,
            MultipartFile xmlFile) {
        FakeRoom fakeRoom = fakeRoomRepository.findById(fakeRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Fake room not found. id=" + fakeRoomId));

        fakeRoom.updateInfo(requestDto.roomName(), requestDto.description());

        if (imageFile != null && !imageFile.isEmpty()) {
            validateImageFile(imageFile);

            String oldImageUrl = fakeRoom.getDrawingImageUrl();
            String newImageUrl = s3Service.uploadFile(imageFile, "fake-room/images/");
            fakeRoom.updateDrawingImageUrl(newImageUrl);

            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                s3Service.deleteFile(oldImageUrl);
            }
        }

        if (xmlFile != null && !xmlFile.isEmpty()) {
            validateXmlFile(xmlFile);

            String oldXmlFileUrl = fakeRoom.getXmlFileUrl();
            String newXmlFileUrl = s3Service.uploadXmlFile(xmlFile, "fake-room/xml/");
            fakeRoom.updateXmlFileUrl(newXmlFileUrl);

            if (oldXmlFileUrl != null && !oldXmlFileUrl.isBlank()) {
                s3Service.deleteFile(oldXmlFileUrl);
            }
        }

        FakeRoom updatedFakeRoom = fakeRoomRepository.save(fakeRoom);
        return FakeRoomResponseDto.from(updatedFakeRoom);
    }

    @Transactional
    public void deleteFakeRoom(Long fakeRoomId) {
        FakeRoom fakeRoom = fakeRoomRepository.findById(fakeRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Fake room not found. id=" + fakeRoomId));

        s3Service.deleteFile(fakeRoom.getDrawingImageUrl());
        s3Service.deleteFile(fakeRoom.getXmlFileUrl());
        fakeRoomRepository.delete(fakeRoom);
    }

    private void validateImageFile(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is required.");
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }
    }

    private void validateXmlFile(MultipartFile xmlFile) {
        if (xmlFile == null || xmlFile.isEmpty()) {
            throw new IllegalArgumentException("XML file is required.");
        }

        String originalFilename = xmlFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".xml")) {
            throw new IllegalArgumentException("Only XML files are allowed.");
        }
    }

    private void scheduleRoom3DNotification(FakeRoom fakeRoom) {
        Room3DResponseMessage response = Room3DResponseMessage.builder()
                .room3dId(fakeRoom.getId())
                .memberId(fakeRoom.getId())
                .status("SUCCESS")
                .xmlFileUrl(fakeRoom.getXmlFileUrl())
                .message("Room3D generation completed.")
                .timestamp(System.currentTimeMillis())
                .build();

        String drawingImageUrl = fakeRoom.getDrawingImageUrl() != null
            ? fakeRoom.getDrawingImageUrl()
            : fakeRoom.getXmlFileUrl();

        taskScheduler.schedule(
                () -> webSocketNotificationService.sendRoom3DGenerationNotification(response, drawingImageUrl),
                Instant.now().plusSeconds(5));
    }
}
