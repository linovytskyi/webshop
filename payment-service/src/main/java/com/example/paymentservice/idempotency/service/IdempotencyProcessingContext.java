package com.example.paymentservice.idempotency.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdempotencyProcessingContext {

    private final String key;
    private final String requestHash;
    private final String httpMethod;
    private final String requestPath;
}
