package com.example.myroom.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myroom.domain.auth.dto.request.AuthLoginRequestDto;
import com.example.myroom.domain.auth.dto.request.AuthRegisterRequestDto;
import com.example.myroom.domain.auth.dto.response.AuthLoginResponseDto;
import com.example.myroom.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerMember(
            @Valid @RequestBody AuthRegisterRequestDto memberRequestDto) {
        authService.registerMember(memberRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register-admin")
    public ResponseEntity<Void> registerAdmin(
            @Valid @RequestBody AuthRegisterRequestDto adminRequestDto) {
        authService.registerAdmin(adminRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDto> login(
            @Valid @RequestBody AuthLoginRequestDto requestDto) {
        AuthLoginResponseDto responseDto = authService.login(requestDto);
        return ResponseEntity.ok().body(responseDto);
    }

}
