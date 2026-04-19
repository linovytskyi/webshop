package com.example.paymentservice.idempotency;

public final class IdempotencyConstants {

    public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    public static final String IDEMPOTENCY_REQUEST_ATTRIBUTE = "idempotencyRecord";

    private IdempotencyConstants() {
    }
}
