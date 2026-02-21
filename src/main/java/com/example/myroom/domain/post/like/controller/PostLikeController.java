package com.example.myroom.domain.post.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.post.like.dto.response.PostLikeResponseDto;
import com.example.myroom.domain.post.like.service.PostLikeService;
import com.example.myroom.global.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostLikeController implements PostLikeApi {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/likes")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PostLikeResponseDto> like(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails member) {
        PostLikeResponseDto responseDto = postLikeService.like(postId, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}/likes")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PostLikeResponseDto> unlike(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails member) {
        PostLikeResponseDto responseDto = postLikeService.unlike(postId, member.getId());
        return ResponseEntity.ok(responseDto);
    }
}
