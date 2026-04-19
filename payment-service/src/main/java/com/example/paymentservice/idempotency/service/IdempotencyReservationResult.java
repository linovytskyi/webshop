package com.example.paymentservice.idempotency.service;

import com.example.paymentservice.idempotency.model.IdempotencyRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdempotencyReservationResult {

    private final IdempotencyRecord record;
    private final boolean reservedByCurrentRequest;
}
