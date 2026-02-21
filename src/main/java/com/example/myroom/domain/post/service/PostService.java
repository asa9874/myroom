package com.example.myroom.domain.post.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;
import com.example.myroom.domain.post.dto.request.PostCreateRequestDto;
import com.example.myroom.domain.post.dto.request.PostUpdateRequestDto;
import com.example.myroom.domain.post.dto.response.PostResponseDto;
import com.example.myroom.domain.post.like.repository.PostLikeRepository;
import com.example.myroom.domain.post.model.Category;
import com.example.myroom.domain.post.model.Post;
import com.example.myroom.domain.post.model.VisibilityScope;
import com.example.myroom.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final Model3DRepository model3DRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto requestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "를 찾을 수 없습니다."));

        Model3D model3D = model3DRepository.findById(requestDto.model3dId())
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + requestDto.model3dId() + "를 찾을 수 없습니다."));

        if (!isOwner(model3D.getCreatorId(), memberId)) {
            throw new IllegalArgumentException("3D 모델에 접근할 권한이 없습니다. 본인이 생성한 3D 모델만 게시글에 첨부할 수 있습니다.");
        }

        Post post = Post.builder()
                .member(member)
                .model3D(model3D)
                .title(requestDto.title())
                .content(requestDto.content())
                .category(requestDto.category())
                .visibilityScope(
                        requestDto.visibilityScope() != null ? requestDto.visibilityScope() : VisibilityScope.PUBLIC)
                .build();

        Post savedPost = postRepository
                .save(post);
        log.info("게시글이 생성되었습니다. ID: {}, 제목: {}", savedPost.getId(), savedPost.getTitle());

        return PostResponseDto.from(savedPost, 0L);
    }

    @Transactional
    public PostResponseDto getPostById(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 " + postId + "를 찾을 수 없습니다."));

        // 비공개 게시글인 경우 작성자만 조회 가능
        if (post.getVisibilityScope() == VisibilityScope.PRIVATE && !post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("비공개 게시글에 접근할 권한이 없습니다.");
        }

        post.incrementViewCount(); // 조회수 증가
        postRepository.save(post); // 변경된 조회수 저장
        long likeCount = postLikeRepository.countByPostId(postId);
        return PostResponseDto.from(post, likeCount);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto requestDto, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 " + postId + "를 찾을 수 없습니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        Model3D model3D = model3DRepository.findById(requestDto.model3dId())
                .orElseThrow(() -> new IllegalArgumentException("3D 모델 " + requestDto.model3dId() + "를 찾을 수 없습니다."));

        if (!isOwner(model3D.getCreatorId(), memberId)) {
            throw new IllegalArgumentException("3D 모델에 접근할 권한이 없습니다. 본인이 생성한 3D 모델만 게시글에 첨부할 수 있습니다.");
        }

        post.update(
                requestDto.title(),
                requestDto.content(),
                requestDto.category(),
                requestDto.visibilityScope(),
                model3D);

        Post updatedPost = postRepository.save(post);
        log.info("게시글이 수정되었습니다. ID: {}, 제목: {}", updatedPost.getId(), updatedPost.getTitle());

        long likeCount = postLikeRepository.countByPostId(postId);
        return PostResponseDto.from(updatedPost, likeCount);
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 " + postId + "를 찾을 수 없습니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
        log.info("게시글이 삭제되었습니다. ID: {}", postId);
    }

    public Page<PostResponseDto> getPublicPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByVisibilityScopeOrderByCreatedAtDesc(VisibilityScope.PUBLIC, pageable);
        return posts.map(post -> PostResponseDto.from(post, postLikeRepository.countByPostId(post.getId())));
    }

    public Page<PostResponseDto> getPostsByCategory(Category category, Pageable pageable) {
        Page<Post> posts = postRepository.findByVisibilityScopeAndCategoryOrderByCreatedAtDesc(
                VisibilityScope.PUBLIC, category, pageable);
        return posts.map(post -> PostResponseDto.from(post, postLikeRepository.countByPostId(post.getId())));
    }

    public Page<PostResponseDto> getMyPosts(Long memberId, Pageable pageable) {
        Page<Post> posts = postRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return posts.map(post -> PostResponseDto.from(post, postLikeRepository.countByPostId(post.getId())));
    }

    public Page<PostResponseDto> searchPosts(String title, Category category, Long memberId, boolean isMyPost,
            Pageable pageable) {
        Page<Post> posts;

        if (isMyPost) {
            // 내 게시글 검색
            posts = postRepository.findByMemberIdAndTitleContainingOrderByCreatedAtDesc(memberId, title, pageable);
        } else {
            // 공개 게시글 검색
            if (category != null) {
                posts = postRepository.findByVisibilityScopeAndCategoryAndTitleContainingOrderByCreatedAtDesc(
                        VisibilityScope.PUBLIC, category, title, pageable);
            } else {
                posts = postRepository.findByVisibilityScopeAndTitleContainingOrderByCreatedAtDesc(
                        VisibilityScope.PUBLIC, title, pageable);
            }
        }

        return posts.map(post -> PostResponseDto.from(post, postLikeRepository.countByPostId(post.getId())));
    }

    private boolean isOwner(Long modelCreatorId, Long memberId) {
        return modelCreatorId.equals(memberId);
    }
}