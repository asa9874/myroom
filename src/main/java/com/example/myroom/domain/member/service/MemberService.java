package com.example.myroom.domain.member.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.myroom.domain.image.S3ImageUploadService;
import com.example.myroom.domain.member.dto.request.MemberUpdateRequestDto;
import com.example.myroom.domain.member.dto.response.MemberResponseDto;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3ImageUploadService s3ImageUploadService;


    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다."));
        return MemberResponseDto.from(member);
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

    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다.");
        }
        memberRepository.deleteById(memberId);
    }
}
