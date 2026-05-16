package com.example.myroom.domain.member.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.image.S3ImageUploadService;
import com.example.myroom.domain.bookmark.repository.Model3DBookmarkRepository;
import com.example.myroom.domain.comment.repository.CommentRepository;
import com.example.myroom.domain.member.dto.request.MemberUpdateRequestDto;
import com.example.myroom.domain.member.dto.response.MemberActivityCountResponseDto;
import com.example.myroom.domain.member.dto.response.MemberResponseDto;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.domain.model3D.service.Model3DService;
import com.example.myroom.domain.model3D.repository.Model3DRepository;
import com.example.myroom.domain.post.like.repository.PostLikeRepository;
import com.example.myroom.domain.post.repository.PostRepository;
import com.example.myroom.domain.recommand.repository.RecommandHistoryRepository;
import com.example.myroom.domain.room3D.repository.Room3DRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3ImageUploadService s3ImageUploadService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final Model3DRepository model3DRepository;
    private final PostLikeRepository postLikeRepository;
    private final Model3DBookmarkRepository model3DBookmarkRepository;
    private final Room3DRepository room3DRepository;
    private final RecommandHistoryRepository recommandHistoryRepository;
    private final Model3DService model3DService;


    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다."));
        return MemberResponseDto.from(member);
    }

    public MemberActivityCountResponseDto getMemberActivityCounts(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다.");
        }

        long postCount = postRepository.countByMemberId(memberId);
        long commentCount = commentRepository.countByMemberId(memberId);
        long model3dCount = model3DRepository.countByCreatorId(memberId);

        return MemberActivityCountResponseDto.of(postCount, commentCount, model3dCount);
    }

    public List<MemberResponseDto> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponseDto::from)
                .toList();
    }

    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto updateRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다."));
        if(!member.getEmail().equals(updateRequestDto.email()) && memberRepository.existsByEmail(updateRequestDto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + updateRequestDto.email());
        }
        member.update(updateRequestDto.name(), updateRequestDto.email());
        Member updatedMember = memberRepository.save(member);
        return MemberResponseDto.from(updatedMember);
    }

    public MemberResponseDto updateProfileImage(Long memberId, MultipartFile imageFile) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다."));

        try {
            String profileImageUrl = s3ImageUploadService.uploadProfileImage(imageFile);
            member.updateProfileImageUrl(profileImageUrl);
            Member updatedMember = memberRepository.save(member);
            return MemberResponseDto.from(updatedMember);
        } catch (IOException e) {
            throw new IllegalArgumentException("프로필 이미지 업로드에 실패했습니다.", e);
        }
    }

    @Transactional
    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다.");
        }

        List<Long> model3dIds = model3DRepository.findIdsByCreatorId(memberId);
        for (Long model3dId : model3dIds) {
            model3DService.deleteModel3D(model3dId, memberId);
        }

        List<Long> postIds = postRepository.findIdsByMemberId(memberId);
        if (!postIds.isEmpty()) {
            commentRepository.deleteByPostIdIn(postIds);
            postLikeRepository.deleteByPostIdIn(postIds);
            postRepository.deleteByMemberId(memberId);
        }

        commentRepository.deleteByMemberId(memberId);
        postLikeRepository.deleteByMemberId(memberId);
        model3DBookmarkRepository.deleteByMemberId(memberId);
        room3DRepository.deleteByMemberId(memberId);
        recommandHistoryRepository.deleteByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }
}
