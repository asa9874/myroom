package com.example.myroom.domain.comment.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.myroom.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.myroom.domain.comment.dto.response.CommentResponseDto;
import com.example.myroom.domain.comment.service.CommentService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestBody CommentCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails member) {
        CommentResponseDto responseDto = commentService.createComment(requestDto, member.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(
            @PathVariable Long commentId) {
        CommentResponseDto responseDto = commentService.getCommentById(commentId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPostId(
            @PathVariable Long postId) {
        List<CommentResponseDto> responseDtos = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/post/{postId}/page")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByPostIdWithPagination(
            @PathVariable Long postId,
            Pageable pageable) {
        Page<CommentResponseDto> responseDtos = commentService.getCommentsByPostId(postId, pageable);
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<CommentResponseDto>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails member,
            Pageable pageable) {
        Page<CommentResponseDto> responseDtos = commentService.getCommentsByMemberId(member.getId(), pageable);
        return ResponseEntity.ok(responseDtos);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails member) {
        CommentResponseDto responseDto = commentService.updateComment(commentId, requestDto, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails member) {
        commentService.deleteComment(commentId, member.getId());
        return ResponseEntity.noContent().build();
    }
}