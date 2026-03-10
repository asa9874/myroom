package com.example.myroom.domain.room3D.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.room3D.dto.request.Room3DSessionCreateRequestDto;
import com.example.myroom.domain.room3D.dto.request.Room3DSessionUpdateInfoRequestDto;
import com.example.myroom.domain.room3D.dto.response.Room3DSessionResponseDto;
import com.example.myroom.domain.room3D.service.Room3DSessionService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room3d/sessions")
public class Room3DSessionController implements Room3DSessionApi {

    private final Room3DSessionService sessionService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room3DSessionResponseDto> createSession(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("data") @Valid Room3DSessionCreateRequestDto requestDto,
            @RequestPart(value = "xmlFile", required = false) MultipartFile xmlFile) {
        
        Room3DSessionResponseDto response = sessionService.createSession(
                userDetails.getId(), 
                requestDto, 
                xmlFile
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<Room3DSessionResponseDto> getSessionById(
            @PathVariable(name = "sessionId") Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<Room3DSessionResponseDto>> getMySessionsPaginated(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<Room3DSessionResponseDto> sessions = sessionService.getMySessionsPaginated(
                userDetails.getId(), 
                pageable
        );
        return ResponseEntity.ok(sessions);
    }

    @PutMapping("/{sessionId}/xml")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room3DSessionResponseDto> updateSessionXml(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "sessionId") Long sessionId,
            @RequestPart("xmlFile") MultipartFile xmlFile) {
        
        Room3DSessionResponseDto response = sessionService.updateSessionXml(
                sessionId, 
                userDetails.getId(), 
                xmlFile
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sessionId}/info")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room3DSessionResponseDto> updateSessionInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "sessionId") Long sessionId,
            @RequestBody @Valid Room3DSessionUpdateInfoRequestDto requestDto) {
        
        Room3DSessionResponseDto response = sessionService.updateSessionInfo(
                sessionId, 
                userDetails.getId(), 
                requestDto
        );
        return ResponseEntity.ok(response);
    }
}
