package com.example.myroom.domain.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.member.dto.request.MemberUpdateRequestDto;
import com.example.myroom.domain.member.dto.response.MemberResponseDto;
import com.example.myroom.domain.member.service.MemberService;
import com.example.myroom.global.jwt.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController implements MemberApi {
    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> getMemberById(
            @PathVariable(name = "memberId") Long memberId) {
        MemberResponseDto responseDto = memberService.getMemberById(memberId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MemberResponseDto> getCurrentMember(
            @AuthenticationPrincipal CustomUserDetails member) {
        MemberResponseDto responseDto = memberService.getMemberById(member.getId());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<MemberResponseDto>> getAllMembers() {
        List<MemberResponseDto> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }
    
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponseDto> updateMember(
            @PathVariable(name = "memberId") Long memberId,
            @Valid @RequestBody MemberUpdateRequestDto updateRequestDto) {
        MemberResponseDto responseDto = memberService.updateMember(memberId, updateRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable(name = "memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

}
