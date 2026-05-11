package com.example.myroom.domain.bookmark.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.bookmark.dto.response.Model3DBookmarkResponseDto;
import com.example.myroom.domain.bookmark.service.Model3DBookmarkService;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model3ds")
public class Model3DBookmarkController implements Model3DBookmarkApi {

    private final Model3DBookmarkService bookmarkService;

    @PostMapping("/{model3dId}/bookmarks")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Model3DBookmarkResponseDto> bookmark(
            @PathVariable(name = "model3dId") Long model3dId,
            @AuthenticationPrincipal CustomUserDetails member) {
        Model3DBookmarkResponseDto responseDto = bookmarkService.bookmark(model3dId, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{model3dId}/bookmarks")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Model3DBookmarkResponseDto> unbookmark(
            @PathVariable(name = "model3dId") Long model3dId,
            @AuthenticationPrincipal CustomUserDetails member) {
        Model3DBookmarkResponseDto responseDto = bookmarkService.unbookmark(model3dId, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{model3dId}/bookmarks/me")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Boolean>> isBookmarked(
            @PathVariable(name = "model3dId") Long model3dId,
            @AuthenticationPrincipal CustomUserDetails member) {
        boolean bookmarked = bookmarkService.isBookmarked(model3dId, member.getId());
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }

    @GetMapping("/bookmarks/my")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<Model3DResponseDto>> getMyBookmarkedModel3Ds(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails member) {
        Page<Model3DResponseDto> responseDtos = bookmarkService.getMyBookmarkedModel3Ds(member.getId(), pageable);
        return ResponseEntity.ok(responseDtos);
    }
}
