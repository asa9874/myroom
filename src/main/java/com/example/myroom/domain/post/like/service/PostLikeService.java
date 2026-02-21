package com.example.myroom.domain.post.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.post.like.dto.response.PostLikeResponseDto;
import com.example.myroom.domain.post.like.model.PostLike;
import com.example.myroom.domain.post.like.repository.PostLikeRepository;
import com.example.myroom.domain.post.model.Post;
import com.example.myroom.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PostLikeResponseDto like(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 " + postId + "를 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "를 찾을 수 없습니다."));

        if (postLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }

        PostLike postLike = PostLike.builder()
                .post(post)
                .member(member)
                .build();

        postLikeRepository.save(postLike);
        log.info("좋아요 등록 - 게시글 ID: {}, 회원 ID: {}", postId, memberId);

        long likeCount = postLikeRepository.countByPostId(postId);
        return PostLikeResponseDto.of(postId, likeCount, true);
    }

    @Transactional
    public PostLikeResponseDto unlike(Long postId, Long memberId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 " + postId + "를 찾을 수 없습니다."));

        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "를 찾을 수 없습니다."));

        PostLike postLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new IllegalStateException("좋아요를 누르지 않은 게시글입니다."));

        postLikeRepository.delete(postLike);
        log.info("좋아요 취소 - 게시글 ID: {}, 회원 ID: {}", postId, memberId);

        long likeCount = postLikeRepository.countByPostId(postId);
        return PostLikeResponseDto.of(postId, likeCount, false);
    }
}
