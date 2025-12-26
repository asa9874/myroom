package com.example.myroom.domain.recommand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.recommand.dto.request.RoomImageUploadRequestDto;
import com.example.myroom.domain.recommand.dto.response.RoomImageResponseDto;
import com.example.myroom.domain.recommand.service.RoomImageService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations/{sessionId}/room-image")
public class RoomImageController implements RoomImageApi {
    private final RoomImageService roomImageService;

    @GetMapping("")
    public ResponseEntity<RoomImageResponseDto> getRoomImageById(
            @PathVariable(name = "sessionId") Long sessionId) {
        RoomImageResponseDto responseDto = roomImageService.getRoomImageById(sessionId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomImageResponseDto> uploadRoomImage(
            @Valid @RequestBody RoomImageUploadRequestDto uploadRequestDto,
            @AuthenticationPrincipal CustomUserDetails member) {
        RoomImageResponseDto responseDto = roomImageService.uploadRoomImage(uploadRequestDto, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoomImage(
            @AuthenticationPrincipal CustomUserDetails member) {
        roomImageService.deleteRoomImage(null, member.getId());
        return ResponseEntity.ok().build();
    }

}
