package com.example.myroom.global.exception;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedError(Exception ex) {
        String apiResponse = "예상치 못한 오류: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        String apiResponse = "JWT 오류: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        String apiResponse = "접근 거부: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        String apiResponse = "엔티티 찾기 오류: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        String apiResponse = "잘못된 인자 오류: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    //DTO 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(msg);
    }

}
