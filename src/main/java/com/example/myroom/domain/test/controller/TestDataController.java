package com.example.myroom.domain.test.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.test.dto.TestDataResultDto;
import com.example.myroom.domain.test.service.TestDataService;
import com.example.myroom.global.jwt.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-data")
public class TestDataController implements TestDataApi {

    private final TestDataService testDataService;

    @PostMapping("/members")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> createTestMembers(
            @RequestParam(value = "count", defaultValue = "5") int count,
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.info("관리자 {}가 {}개의 테스트 회원 생성을 요청했습니다.", admin.getUsername(), count);
        TestDataResultDto result = testDataService.createTestMembers(count);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/model3ds")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> createTestModel3Ds(
            @RequestParam(value = "count", defaultValue = "10") int count,
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.info("관리자 {}가 {}개의 테스트 3D 모델 생성을 요청했습니다.", admin.getUsername(), count);
        TestDataResultDto result = testDataService.createTestModel3Ds(count);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/posts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> createTestPosts(
            @RequestParam(value = "count", defaultValue = "20") int count,
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.info("관리자 {}가 {}개의 테스트 게시글 생성을 요청했습니다.", admin.getUsername(), count);
        TestDataResultDto result = testDataService.createTestPosts(count);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/full-set")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> createFullTestDataSet(
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.info("관리자 {}가 전체 테스트 데이터 세트 생성을 요청했습니다.", admin.getUsername());
        TestDataResultDto result = testDataService.createFullTestDataSet();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> deleteAllTestData(
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.warn("⚠️ 관리자 {}가 모든 테스트 데이터 삭제를 요청했습니다.", admin.getUsername());
        TestDataResultDto result = testDataService.deleteAllTestData();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/members")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> deleteTestMembers(
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.info("관리자 {}가 테스트 회원 삭제를 요청했습니다.", admin.getUsername());
        TestDataResultDto result = testDataService.deleteTestMembers();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/model3ds")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> deleteTestModel3Ds(
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.info("관리자 {}가 테스트 3D 모델 삭제를 요청했습니다.", admin.getUsername());
        TestDataResultDto result = testDataService.deleteTestModel3Ds();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/posts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TestDataResultDto> deleteTestPosts(
            @AuthenticationPrincipal CustomUserDetails admin) {
        log.info("관리자 {}가 테스트 게시글 삭제를 요청했습니다.", admin.getUsername());
        TestDataResultDto result = testDataService.deleteTestPosts();
        return ResponseEntity.ok(result);
    }
}