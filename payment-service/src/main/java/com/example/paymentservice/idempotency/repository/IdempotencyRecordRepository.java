package com.example.paymentservice.idempotency.repository;

import com.example.paymentservice.idempotency.model.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, String> {
}
