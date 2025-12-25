package com.example.myroom.domain.member.dto.request;

public record MemberLoginRequestDto(
        String email,
        String password
) {
    
}
