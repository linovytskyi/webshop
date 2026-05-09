package com.example.paymentservice.controller;

import com.example.paymentservice.controller.dto.ErrorResponse;
import com.example.paymentservice.idempotency.service.IdempotencyProcessingException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class IdempotencyProcessingExceptionHandler {

    @ExceptionHandler(IdempotencyProcessingException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IdempotencyProcessingException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .timestamp(LocalDateTime.now().toString())
                        .status(HttpStatus.CONFLICT.value())
                        .error(exception.getMessage())
                        .build());
    }
}
