package com.example.myroom.domain.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myroom.domain.test.dto.TestDataResultDto;
import com.example.myroom.global.jwt.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "🧪 테스트 데이터", description = "테스트 데이터 생성/삭제 API - 개발 및 테스트 환경에서 사용하는 관리자 전용 기능입니다.")
public interface TestDataApi {

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "테스트 회원 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TestDataResultDto.class),
                    examples = @ExampleObject(
                        name = "테스트 회원 생성 성공",
                        value = """
                        {
                            "created_count": 5,
                            "message": "5개의 테스트 회원이 생성되었습니다.",
                            "created_ids": [1, 2, 3, 4, 5]
                        }
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "관리자 권한 필요", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "테스트 회원들 생성",
        description = "지정된 개수만큼 테스트 회원들을 생성합니다. 관리자 권한이 필요합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/members")
    ResponseEntity<TestDataResultDto> createTestMembers(
            @Parameter(description = "생성할 회원 수", example = "5")
            @RequestParam(defaultValue = "5") int count,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "테스트 3D 모델 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TestDataResultDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "테스트 회원이 존재하지 않음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "테스트 3D 모델들 생성",
        description = "지정된 개수만큼 테스트 3D 모델들을 생성합니다. 기존 회원들에게 랜덤하게 배정됩니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/model3ds")
    ResponseEntity<TestDataResultDto> createTestModel3Ds(
            @Parameter(description = "생성할 3D 모델 수", example = "10")
            @RequestParam(defaultValue = "10") int count,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "테스트 게시글 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TestDataResultDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "테스트 회원이 존재하지 않음", 
                content = @Content(schema = @Schema(hidden = true))
            )
        }
    )
    @Operation(
        summary = "테스트 게시글들 생성",
        description = "지정된 개수만큼 테스트 게시글들을 생성합니다. 기존 회원들과 3D 모델들을 랜덤하게 연결합니다.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/posts")
    ResponseEntity<TestDataResultDto> createTestPosts(
            @Parameter(description = "생성할 게시글 수", example = "20")
            @RequestParam(defaultValue = "20") int count,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "전체 테스트 데이터 세트 생성 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TestDataResultDto.class),
                    examples = @ExampleObject(
                        name = "전체 테스트 데이터 생성 성공",
                        value = """
                        {
                            "created_count": 50,
                            "message": "전체 테스트 데이터 세트가 생성되었습니다. (회원: 10, 3D모델: 15, 게시글: 25)",
                            "created_ids": [10, 15, 25]
                        }
                        """
                    )
                )
            )
        }
    )
    @Operation(
        summary = "전체 테스트 데이터 세트 생성",
        description = "완전한 테스트 환경을 위한 모든 데이터를 생성합니다. (회원 10명, 3D모델 15개, 게시글 25개)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/full-set")
    ResponseEntity<TestDataResultDto> createFullTestDataSet(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );

    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200", 
                description = "모든 데이터 삭제 성공",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TestDataResultDto.class)
                )
            )
        }
    )
    @Operation(
        summary = "모든 데이터 삭제 (위험!)",
        description = "⚠️ 위험: 데이터베이스의 모든 데이터를 삭제합니다! 테스트 데이터뿐만 아니라 실제 프로덕션 데이터도 모두 삭제됩니다. 개발 환경에서만 사용하세요!",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/all")
    ResponseEntity<TestDataResultDto> deleteAllTestData(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );

    @Operation(
        summary = "모든 회원 데이터 삭제 (위험!)",
        description = "⚠️ 위험: 데이터베이스의 모든 회원 데이터를 삭제합니다. (테스트 + 실제 데이터 포함)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/members")
    ResponseEntity<TestDataResultDto> deleteTestMembers(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );

    @Operation(
        summary = "모든 3D 모델 데이터 삭제 (위험!)",
        description = "⚠️ 위험: 데이터베이스의 모든 3D 모델 데이터를 삭제합니다. (테스트 + 실제 데이터 포함)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/model3ds")
    ResponseEntity<TestDataResultDto> deleteTestModel3Ds(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );

    @Operation(
        summary = "모든 게시글 데이터 삭제 (위험!)",
        description = "⚠️ 위험: 데이터베이스의 모든 게시글 데이터를 삭제합니다. (테스트 + 실제 데이터 포함)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/posts")
    ResponseEntity<TestDataResultDto> deleteTestPosts(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails admin
    );
}