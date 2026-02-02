package com.example.myroom.domain.member.dto.response;

import com.example.myroom.domain.member.model.Member;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "회원 정보 응답 DTO")
@JsonNaming(SnakeCaseStrategy.class)
public record MemberResponseDto(
    @Schema(
        description = "회원 고유 식별자 (자동 생성)", 
        requiredMode = RequiredMode.REQUIRED,
        example = "1"
    )
    Long id,
    
    @Schema(
        description = "회원 이름", 
        requiredMode = RequiredMode.REQUIRED,
        example = "홍길동"
    )
    String username,
    
    @Schema(
        description = "회원 이메일 주소", 
        requiredMode = RequiredMode.REQUIRED,
        example = "user@example.com"
    )
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