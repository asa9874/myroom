package com.example.myroom.domain.post.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.post.dto.request.PostCreateRequestDto;
import com.example.myroom.domain.post.dto.request.PostUpdateRequestDto;
import com.example.myroom.domain.post.dto.response.PostResponseDto;
import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.service.PostService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController implements PostApi {

    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody PostCreateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails member) {
        System.out.println("Creating post for member ID: " + member.getId());
        System.out.println("Model3D ID: " + requestDto.model3dId());
        System.out.println("Title: " + requestDto.title());
        PostResponseDto responseDto = postService.createPost(requestDto, member.getId());
        System.out.println("Created post with ID: " + responseDto.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PostResponseDto> getPostById(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails member) {
        PostResponseDto responseDto = postService.getPostById(postId, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable(name = "postId") Long postId,
            @Valid @RequestBody PostUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails member) {
        PostResponseDto responseDto = postService.updatePost(postId, requestDto, member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePost(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal CustomUserDetails member) {
        postService.deletePost(postId, member.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PostResponseDto>> getPublicPosts(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails member) {
        Page<PostResponseDto> posts = postService.getPublicPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PostResponseDto>> getPostsByCategory(
            @PathVariable(value = "category") Category category,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails member) {
        Page<PostResponseDto> posts = postService.getPostsByCategory(category, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PostResponseDto>> getMyPosts(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails member) {
        Page<PostResponseDto> posts = postService.getMyPosts(member.getId(), pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PostResponseDto>> searchPosts(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "myPost", required = false, defaultValue = "false") boolean myPost,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails member) {
        Page<PostResponseDto> posts = postService.searchPosts(title, category, member.getId(), myPost, pageable);
        return ResponseEntity.ok(posts);
    }
}