package com.example.paymentservice.idempotency.service;

public class MissingIdempotencyKeyException extends RuntimeException {

    public MissingIdempotencyKeyException(String message) {
        super(message);
    }
}
