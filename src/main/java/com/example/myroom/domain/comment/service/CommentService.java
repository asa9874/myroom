package com.example.myroom.domain.comment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myroom.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.myroom.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.myroom.domain.comment.dto.response.CommentResponseDto;
import com.example.myroom.domain.comment.model.Comment;
import com.example.myroom.domain.comment.repository.CommentRepository;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.post.model.Post;
import com.example.myroom.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(CommentCreateRequestDto requestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "를 찾을 수 없습니다."));

        Post post = postRepository.findById(requestDto.postId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 " + requestDto.postId() + "를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content(requestDto.content())
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("댓글이 생성되었습니다. 댓글 ID: {}, 게시글 ID: {}, 작성자 ID: {}", 
                savedComment.getId(), post.getId(), member.getId());
        
        return CommentResponseDto.from(savedComment);
    }

    public CommentResponseDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 " + commentId + "를 찾을 수 없습니다."));
        
        return CommentResponseDto.from(comment);
    }

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAt(postId);
        return comments.stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    public Page<CommentResponseDto> getCommentsByPostId(Long postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        return comments.map(CommentResponseDto::from);
    }

    public Page<CommentResponseDto> getCommentsByMemberId(Long memberId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByMemberId(memberId, pageable);
        return comments.map(CommentResponseDto::from);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentUpdateRequestDto requestDto, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 " + commentId + "를 찾을 수 없습니다."));

        if (!isOwner(comment.getMember().getId(), memberId)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment.update(requestDto.content());
        Comment updatedComment = commentRepository.save(comment);
        log.info("댓글이 수정되었습니다. 댓글 ID: {}", commentId);
        
        return CommentResponseDto.from(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 " + commentId + "를 찾을 수 없습니다."));

        if (!isOwner(comment.getMember().getId(), memberId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
        log.info("댓글이 삭제되었습니다. 댓글 ID: {}", commentId);
    }

    public long getCommentCountByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private boolean isOwner(Long ownerId, Long memberId) {
        return ownerId.equals(memberId);
    }
}