package com.example.myroom.domain.room3D.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.room3D.dto.request.Room3DCreateRequestDto;
import com.example.myroom.domain.room3D.dto.request.Room3DUpdateRequestDto;
import com.example.myroom.domain.room3D.dto.response.Room3DResponseDto;
import com.example.myroom.domain.room3D.service.Room3DService;
import com.example.myroom.global.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room3d")
public class Room3DController implements Room3DApi {

    private final Room3DService room3DService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room3DResponseDto> createRoom3D(
            @RequestPart(value = "image", required = true) MultipartFile imageFile,
            @RequestParam(value = "room_name") String roomName,
            @RequestParam(value = "description", required = false) String description,
            @AuthenticationPrincipal CustomUserDetails member) {
        Room3DCreateRequestDto requestDto = new Room3DCreateRequestDto(roomName, description);
        Room3DResponseDto responseDto = room3DService.createRoom3D(imageFile, requestDto, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping(value = "/{room3dId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room3DResponseDto> updateRoom3D(
            @PathVariable(name = "room3dId") Long room3dId,
            @RequestPart(value = "xml_file", required = false) MultipartFile xmlFile,
            @RequestParam(value = "room_name", required = false) String roomName,
            @RequestParam(value = "description", required = false) String description,
            @AuthenticationPrincipal CustomUserDetails member) {
        Room3DUpdateRequestDto requestDto = new Room3DUpdateRequestDto(roomName, description);
        Room3DResponseDto responseDto = room3DService.updateRoom3D(room3dId, requestDto, xmlFile, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{room3dId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room3DResponseDto> getRoom3DById(
            @PathVariable(name = "room3dId") Long room3dId,
            @AuthenticationPrincipal CustomUserDetails member) {
        return ResponseEntity.ok(room3DService.getRoom3DById(room3dId, member.getId()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<Room3DResponseDto>> getMyRoom3Ds(
            @AuthenticationPrincipal CustomUserDetails member,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(room3DService.getMyRoom3Ds(member.getId(), pageable));
    }

    @DeleteMapping("/{room3dId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom3D(
            @PathVariable(name = "room3dId") Long room3dId,
            @AuthenticationPrincipal CustomUserDetails member) {
        room3DService.deleteRoom3D(room3dId, member.getId());
        return ResponseEntity.noContent().build();
    }
}
