package com.example.myroom.domain.bookmark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myroom.domain.bookmark.dto.response.Model3DBookmarkResponseDto;
import com.example.myroom.domain.bookmark.model.Model3DBookmark;
import com.example.myroom.domain.bookmark.repository.Model3DBookmarkRepository;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.model3D.dto.response.Model3DResponseDto;
import com.example.myroom.domain.model3D.model.Model3D;
import com.example.myroom.domain.model3D.repository.Model3DRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Model3DBookmarkService {

    private final Model3DBookmarkRepository bookmarkRepository;
    private final Model3DRepository model3DRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Model3DBookmarkResponseDto bookmark(Long model3dId, Long memberId) {
        Model3D model3D = getAccessibleModel(model3dId, memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member " + memberId + " not found."));

        if (bookmarkRepository.existsByModel3DIdAndMemberId(model3dId, memberId)) {
            throw new IllegalStateException("Model3D is already bookmarked.");
        }

        Model3DBookmark bookmark = Model3DBookmark.builder()
                .model3D(model3D)
                .member(member)
                .build();

        bookmarkRepository.save(bookmark);
        log.info("Model3D bookmark created - model3dId: {}, memberId: {}", model3dId, memberId);

        return Model3DBookmarkResponseDto.of(model3dId, true);
    }

    @Transactional
    public Model3DBookmarkResponseDto unbookmark(Long model3dId, Long memberId) {
        getAccessibleModel(model3dId, memberId);
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member " + memberId + " not found."));

        Model3DBookmark bookmark = bookmarkRepository.findByModel3DIdAndMemberId(model3dId, memberId)
                .orElseThrow(() -> new IllegalStateException("Model3D is not bookmarked."));

        bookmarkRepository.delete(bookmark);
        log.info("Model3D bookmark removed - model3dId: {}, memberId: {}", model3dId, memberId);

        return Model3DBookmarkResponseDto.of(model3dId, false);
    }

    public boolean isBookmarked(Long model3dId, Long memberId) {
        getAccessibleModel(model3dId, memberId);
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member " + memberId + " not found."));

        return bookmarkRepository.existsByModel3DIdAndMemberId(model3dId, memberId);
    }

    public Page<Model3DResponseDto> getMyBookmarkedModel3Ds(Long memberId, Pageable pageable) {
        Page<Model3D> model3Ds = bookmarkRepository.findBookmarkedModel3DsByMemberId(memberId, pageable);
        return model3Ds.map(Model3DResponseDto::from);
    }

    private Model3D getAccessibleModel(Long model3dId, Long memberId) {
        Model3D model3D = model3DRepository.findById(model3dId)
                .orElseThrow(() -> new IllegalArgumentException("Model3D " + model3dId + " not found."));

        if (!model3D.getCreatorId().equals(memberId) && !Boolean.TRUE.equals(model3D.getIsShared())) {
            throw new IllegalArgumentException("Access to Model3D is not allowed.");
        }

        return model3D;
    }
}
