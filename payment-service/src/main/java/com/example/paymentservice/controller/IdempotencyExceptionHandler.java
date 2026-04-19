package com.example.paymentservice.controller;

import com.example.paymentservice.controller.dto.ErrorResponse;
import com.example.paymentservice.idempotency.service.IdempotencyConflictException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class IdempotencyExceptionHandler {

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IdempotencyConflictException exception) {
        log.warn("Idempotency conflict: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .status(HttpStatus.CONFLICT.value())
                        .error(exception.getMessage())
                        .build());
    }
}

