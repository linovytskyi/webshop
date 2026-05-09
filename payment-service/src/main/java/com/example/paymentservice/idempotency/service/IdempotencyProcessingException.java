package com.example.paymentservice.idempotency.service;

public class IdempotencyProcessingException extends RuntimeException {

    public IdempotencyProcessingException(String message) {
        super(message);
    }
}
