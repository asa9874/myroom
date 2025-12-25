package com.example.myroom.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.myroom.domain.member.dto.request.MemberLoginRequestDto;
import com.example.myroom.domain.member.dto.request.MemberRegisterRequestDto;
import com.example.myroom.domain.member.dto.response.LoginResponseDto;
import com.example.myroom.domain.member.dto.response.MemberResponseDto;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.global.jwt.JwtTokenProvider;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public MemberResponseDto getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 " + memberId + "을(를) 찾을 수 없습니다."));
        return MemberResponseDto.from(member);
    }

    public void registerMember(MemberRegisterRequestDto memberRequestDto) {
        if (memberRepository.existsByEmail(memberRequestDto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + memberRequestDto.email());
        }

        Member member = Member.builder()
                .name(memberRequestDto.name())
                .email(memberRequestDto.email())
                .password(passwordEncoder.encode(memberRequestDto.password()))
                .build();
        memberRepository.save(member);
    }

    public LoginResponseDto login(MemberLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name(), member.getId());
        return new LoginResponseDto(token);
    }
}
