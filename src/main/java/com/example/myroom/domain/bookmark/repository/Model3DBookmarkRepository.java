package com.example.myroom.domain.bookmark.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.myroom.domain.bookmark.model.Model3DBookmark;
import com.example.myroom.domain.model3D.model.Model3D;

public interface Model3DBookmarkRepository extends JpaRepository<Model3DBookmark, Long> {

    @Query("SELECT b FROM Model3DBookmark b WHERE b.model3D.id = :model3dId AND b.member.id = :memberId")
    Optional<Model3DBookmark> findByModel3DIdAndMemberId(
            @Param("model3dId") Long model3dId,
            @Param("memberId") Long memberId);

    @Query("SELECT COUNT(b) > 0 FROM Model3DBookmark b WHERE b.model3D.id = :model3dId AND b.member.id = :memberId")
    boolean existsByModel3DIdAndMemberId(
            @Param("model3dId") Long model3dId,
            @Param("memberId") Long memberId);

    @Query(
        value = "SELECT b.model3D FROM Model3DBookmark b WHERE b.member.id = :memberId AND (b.model3D.creatorId = :memberId OR b.model3D.isShared = true) ORDER BY b.createdAt DESC",
        countQuery = "SELECT COUNT(b) FROM Model3DBookmark b WHERE b.member.id = :memberId AND (b.model3D.creatorId = :memberId OR b.model3D.isShared = true)"
    )
    Page<Model3D> findBookmarkedModel3DsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    void deleteByMemberId(Long memberId);

    void deleteByModel3DId(Long model3dId);
}
