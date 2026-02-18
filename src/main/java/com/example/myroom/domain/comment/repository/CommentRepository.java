package com.example.myroom.domain.comment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.myroom.domain.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member " +
           "WHERE c.post.id = :postId " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findByPostIdOrderByCreatedAt(@Param("postId") Long postId);
    
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member " +
           "JOIN FETCH c.post " +
           "WHERE c.post.id = :postId")
    Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);
    
    @Query("SELECT c FROM Comment c " +
           "JOIN FETCH c.member " +
           "JOIN FETCH c.post " +
           "WHERE c.member.id = :memberId")
    Page<Comment> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
    
    long countByPostId(Long postId);
}