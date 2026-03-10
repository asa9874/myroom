package com.example.myroom.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.myroom.domain.auth.dto.request.AuthLoginRequestDto;
import com.example.myroom.domain.auth.dto.request.AuthRefreshRequestDto;
import com.example.myroom.domain.auth.dto.request.AuthRegisterRequestDto;
import com.example.myroom.domain.auth.dto.response.AuthLoginResponseDto;
import com.example.myroom.domain.auth.dto.response.AuthRefreshResponseDto;
import com.example.myroom.domain.auth.model.UserToken;
import com.example.myroom.domain.auth.repository.UserTokenRepository;
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
    private final UserTokenRepository userTokenRepository;

    @Transactional(readOnly = true)
    public boolean existsEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

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

    @Transactional
    public AuthLoginResponseDto login(AuthLoginRequestDto requestDto) {
        Member member = memberRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(requestDto.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name(), member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);

        // 기존 토큰이 있으면 덮어쓰기, 없으면 새로 저장
        userTokenRepository.findById(member.getId())
                .ifPresentOrElse(
                        userToken -> userToken.update(refreshToken, expiryDate),
                        () -> userTokenRepository.save(UserToken.builder()
                                .userId(member.getId())
                                .refreshToken(refreshToken)
                                .expiryDate(expiryDate)
                                .build())
                );

        return new AuthLoginResponseDto(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthRefreshResponseDto refresh(AuthRefreshRequestDto requestDto) {
        UserToken userToken = userTokenRepository.findByRefreshToken(requestDto.refreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."));

        if (userToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다. 다시 로그인해주세요.");
        }

        Member member = memberRepository.findById(userToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "회원 정보를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name(), member.getId());
        return new AuthRefreshResponseDto(newAccessToken);
    }
}

