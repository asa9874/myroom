package com.example.myroom.domain.member.dto.response;

import com.example.myroom.domain.member.model.Member;

public record MemberResponseDto(
    Long id,
    String username,
    String email
) {
    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(
            member.getId(),
            member.getName(),
            member.getEmail()
        );
    }
} 