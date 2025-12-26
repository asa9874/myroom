package com.example.myroom.domain.member.dto.response;

import com.example.myroom.domain.member.model.Member;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@JsonNaming(SnakeCaseStrategy.class)
public record MemberResponseDto(
    @Schema(description = "회원 ID", requiredMode = RequiredMode.REQUIRED)
    Long id,
    @Schema(description = "회원 이름", requiredMode = RequiredMode.REQUIRED)
    String username,
    @Schema(description = "회원 이메일", requiredMode = RequiredMode.REQUIRED)
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