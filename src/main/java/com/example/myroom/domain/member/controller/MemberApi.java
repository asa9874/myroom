package com.example.myroom.domain.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.myroom.domain.member.dto.request.MemberUpdateRequestDto;
import com.example.myroom.domain.member.dto.response.MemberResponseDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = " 회원", description = "회원 정보 조회 및 관리")
public interface MemberApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "회원 단건 조회", description = "회원 ID로 특정 회원의 정보를 조회")
    @GetMapping("/{memberId}")
    ResponseEntity<MemberResponseDto> getMemberById(
            @Parameter(description = "조회할 회원 ID")
            @PathVariable(name = "memberId") Long memberId
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "현재 사용자 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "현재 로그인 사용자 조회", description = "JWT 토큰으로부터 현재 로그인한 사용자의 정보를 조회")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    ResponseEntity<MemberResponseDto> getCurrentMember(
            @Parameter(description = "현재 인증된 사용자 정보 (JWT 토큰으로부터 자동 주입)", hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "전체 회원 조회 성공")
        }
    )
    @Operation(summary = "전체 회원 조회", description = "모든 회원의 목록을 조회")
    @GetMapping("/")
    ResponseEntity<List<MemberResponseDto>> getAllMembers();

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "회원 정보 수정", description = "특정 회원의 정보를 수정")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{memberId}")
    ResponseEntity<MemberResponseDto> updateMember(
            @Parameter(description = "수정할 회원 ID")
            @PathVariable(name = "memberId") Long memberId,
            @Parameter(description = "수정할 회원 정보")
            @Valid @RequestBody MemberUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "204", description = "회원 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
        }
    )
    @Operation(summary = "회원 삭제", description = "특정 회원을 삭제")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{memberId}")
    ResponseEntity<Void> deleteMember(
            @Parameter(description = "삭제할 회원 ID")
            @PathVariable(name = "memberId") Long memberId
    );

}
