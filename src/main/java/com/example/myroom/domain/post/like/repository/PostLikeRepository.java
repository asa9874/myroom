package com.example.myroom.domain.post.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.myroom.domain.post.like.model.PostLike;
import com.example.myroom.domain.post.model.Post;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 특정 게시글의 좋아요 수 조회 (count 쿼리)
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);

    @Query("SELECT pl.post.id, COUNT(pl) FROM PostLike pl WHERE pl.post.id IN :postIds GROUP BY pl.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
    Optional<PostLike> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 여부 확인
    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.post.id = :postId AND pl.member.id = :memberId")
    boolean existsByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    @Query(
        value = "SELECT pl.post FROM PostLike pl WHERE pl.member.id = :memberId",
        countQuery = "SELECT COUNT(pl) FROM PostLike pl WHERE pl.member.id = :memberId"
    )
    Page<Post> findLikedPostsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
