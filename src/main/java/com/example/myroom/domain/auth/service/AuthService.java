package com.example.myroom.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.myroom.domain.auth.dto.request.AuthLoginRequestDto;
import com.example.myroom.domain.auth.dto.request.AuthRegisterRequestDto;
import com.example.myroom.domain.auth.dto.response.AuthLoginResponseDto;
import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.member.model.Role;
import com.example.myroom.domain.member.repository.MemberRepository;
import com.example.myroom.global.jwt.JwtTokenProvider;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void registerMember(AuthRegisterRequestDto memberRequestDto) {
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

    public void registerAdmin(AuthRegisterRequestDto adminRequestDto) {
        if (memberRepository.existsByEmail(adminRequestDto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + adminRequestDto.email());
        }

        Member admin = Member.builder()
                .name(adminRequestDto.name())
                .email(adminRequestDto.email())
                .password(passwordEncoder.encode(adminRequestDto.password()))
                .build();
        
        admin.updateRole(Role.ADMIN);
        memberRepository.save(admin);
    }

    public AuthLoginResponseDto login(AuthLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name(), member.getId());
        return new AuthLoginResponseDto(token);
    }

}
