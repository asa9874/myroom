package com.example.myroom.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.member.dto.request.MemberLoginRequestDto;
import com.example.myroom.domain.member.dto.request.MemberRegisterRequestDto;
import com.example.myroom.domain.member.dto.response.LoginResponseDto;
import com.example.myroom.domain.member.dto.response.MemberResponseDto;
import com.example.myroom.domain.member.service.MemberService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(
            @PathVariable(name = "memberId") Long memberId) {
        MemberResponseDto responseDto = memberService.getMemberById(memberId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerMember(
            @RequestBody MemberRegisterRequestDto memberRequestDto) {
        memberService.registerMember(memberRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody MemberLoginRequestDto requestDto) {
        LoginResponseDto responseDto = memberService.login(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MemberResponseDto> getCurrentMember(
            @AuthenticationPrincipal CustomUserDetails member) {
        MemberResponseDto responseDto = memberService.getMemberById(member.getId());
        return ResponseEntity.ok(responseDto);
    }

}
