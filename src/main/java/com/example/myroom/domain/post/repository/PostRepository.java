package com.example.myroom.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.model.Post;
import com.example.myroom.domain.post.model.VisibilityScope;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 내 게시글 조회 (모든 공개범위)
    Page<Post> findByMemberId(Long memberId, Pageable pageable);
    long countByMemberId(Long memberId);
    
    // 공개 게시글만 조회 
    Page<Post> findByVisibilityScope(VisibilityScope visibilityScope, Pageable pageable);
    
    // 카테고리별 공개 게시글 조회
    Page<Post> findByVisibilityScopeAndCategory(VisibilityScope visibilityScope, Category category, Pageable pageable);
    
    // 제목 검색 (공개 게시글)
    Page<Post> findByVisibilityScopeAndTitleContaining(VisibilityScope visibilityScope, String title, Pageable pageable);
    
    // 내 게시글 제목 검색
    Page<Post> findByMemberIdAndTitleContaining(Long memberId, String title, Pageable pageable);
    
    // 카테고리 + 제목 검색 (공개 게시글)
    Page<Post> findByVisibilityScopeAndCategoryAndTitleContaining(
        VisibilityScope visibilityScope, Category category, String title, Pageable pageable);
}