package com.example.myroom.domain.member.dto.request;

public record MemberRegisterRequestDto(
    String name,
    String email,
    String password
) {
}
