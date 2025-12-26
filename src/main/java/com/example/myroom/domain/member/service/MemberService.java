package com.example.myroom.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myroom.domain.member.dto.request.MemberUpdateRequestDto;
import com.example.myroom.domain.member.dto.response.MemberResponseDto;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;


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
        if(memberRepository.existsByEmail(updateRequestDto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + updateRequestDto.email());
        }
        member.update(updateRequestDto.name(), updateRequestDto.email());
        Member updatedMember = memberRepository.save(member);
        return MemberResponseDto.from(updatedMember);
    }

    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new IllegalArgumentException("회원 " + memberId + "을 찾을 수 없습니다.");
        }
        memberRepository.deleteById(memberId);
    }
}
