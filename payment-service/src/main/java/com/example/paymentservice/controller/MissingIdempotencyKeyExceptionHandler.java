package com.example.paymentservice.controller;

import com.example.paymentservice.controller.dto.ErrorResponse;
import com.example.paymentservice.idempotency.service.MissingIdempotencyKeyException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class MissingIdempotencyKeyExceptionHandler {

    @ExceptionHandler(MissingIdempotencyKeyException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(MissingIdempotencyKeyException exception) {
        log.warn("Missing idempotency key: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(exception.getMessage())
                        .build());
    }
}
