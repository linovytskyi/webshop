package com.example.paymentservice.repository;

import com.example.paymentservice.model.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMessageId(String messageId);
}