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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "👤 회원", description = "회원 정보 조회 및 관리 API - 회원 CRUD 기능을 제공합니다.")
public interface MemberApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "회원 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponseDto.class),
                    examples = @ExampleObject(
                        name = "회원 조회 성공 응답",
                        value = "{\"id\": 1, \"username\": \"홍길동\", \"email\": \"user@example.com\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "회원을 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "회원 없음",
                        value = "{\"message\": \"해당 회원을 찾을 수 없습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "회원 단건 조회", 
        description = """
            회원 ID로 특정 회원의 정보를 조회합니다.
            
            **응답 예시:**
            ```json
            {
                "id": 1,
                "username": "홍길동",
                "email": "user@example.com"
            }
            ```
            """
    )
    @GetMapping("/{memberId}")
    ResponseEntity<MemberResponseDto> getMemberById(
            @Parameter(
                description = "조회할 회원의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "memberId") Long memberId
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "현재 사용자 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponseDto.class),
                    examples = @ExampleObject(
                        name = "현재 사용자 조회 응답",
                        value = "{\"id\": 1, \"username\": \"홍길동\", \"email\": \"user@example.com\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "인증되지 않음 - 유효하지 않은 토큰", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "인증 실패",
                        value = "{\"message\": \"인증이 필요합니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "현재 로그인 사용자 조회", 
        description = """
            JWT 토큰으로부터 현재 로그인한 사용자의 정보를 조회합니다.
            
            **인증 필요:** Bearer Token
            
            **응답 예시:**
            ```json
            {
                "id": 1,
                "username": "홍길동",
                "email": "user@example.com"
            }
            ```
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    ResponseEntity<MemberResponseDto> getCurrentMember(
            @Parameter(description = "현재 인증된 사용자 정보 (JWT 토큰으로부터 자동 주입)", hidden = true)
            @AuthenticationPrincipal CustomUserDetails member
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "전체 회원 조회 성공",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MemberResponseDto.class)),
                    examples = @ExampleObject(
                        name = "전체 회원 목록 응답",
                        value = "[{\"id\": 1, \"username\": \"홍길동\", \"email\": \"user1@example.com\"}, {\"id\": 2, \"username\": \"김철수\", \"email\": \"user2@example.com\"}]"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "전체 회원 조회", 
        description = """
            시스템에 등록된 모든 회원의 목록을 조회합니다.
            
            **응답 예시:**
            ```json
            [
                {
                    "id": 1,
                    "username": "홍길동",
                    "email": "user1@example.com"
                },
                {
                    "id": 2,
                    "username": "김철수",
                    "email": "user2@example.com"
                }
            ]
            ```
            """
    )
    @GetMapping("/")
    ResponseEntity<List<MemberResponseDto>> getAllMembers();

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "회원 정보 수정 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MemberResponseDto.class),
                    examples = @ExampleObject(
                        name = "수정 성공 응답",
                        value = "{\"id\": 1, \"username\": \"김철수\", \"email\": \"newuser@example.com\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "잘못된 요청 - 유효성 검사 실패", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "유효성 검사 실패",
                        value = "{\"message\": \"이름은 비어 있을 수 없습니다.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "회원을 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "회원 없음",
                        value = "{\"message\": \"해당 회원을 찾을 수 없습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "회원 정보 수정", 
        description = """
            특정 회원의 정보를 수정합니다.
            
            **인증 필요:** Bearer Token
            
            **요청 본문 예시:**
            ```json
            {
                "name": "김철수",
                "email": "newuser@example.com"
            }
            ```
            
            **응답 예시:**
            ```json
            {
                "id": 1,
                "username": "김철수",
                "email": "newuser@example.com"
            }
            ```
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{memberId}")
    ResponseEntity<MemberResponseDto> updateMember(
            @Parameter(
                description = "수정할 회원의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "memberId") Long memberId,
            @Parameter(
                description = "수정할 회원 정보",
                required = true
            )
            @Valid @RequestBody MemberUpdateRequestDto updateRequestDto
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204", 
                description = "회원 삭제 성공",
                content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "회원을 찾을 수 없음", 
                content = @Content(
                    schema = @Schema(hidden = true),
                    examples = @ExampleObject(
                        name = "회원 없음",
                        value = "{\"message\": \"해당 회원을 찾을 수 없습니다.\"}"
                    )
                )
            )
        }
    )
    @Operation(
        summary = "회원 삭제", 
        description = """
            특정 회원을 삭제합니다.
            
            **인증 필요:** Bearer Token
            
            **주의사항:**
            - 삭제된 회원은 복구할 수 없습니다.
            - 회원 삭제 시 해당 회원이 생성한 3D 모델도 함께 삭제될 수 있습니다.
            """
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{memberId}")
    ResponseEntity<Void> deleteMember(
            @Parameter(
                description = "삭제할 회원의 고유 ID",
                required = true,
                example = "1"
            )
            @PathVariable(name = "memberId") Long memberId
    );

}
