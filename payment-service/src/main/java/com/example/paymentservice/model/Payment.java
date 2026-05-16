package com.example.paymentservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
@Schema(description = "Payment entity stored in payment-service")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Payment identifier", example = "10")
    private Long id;

    @Column(unique = true)
    @Schema(description = "Idempotency key from the message that created this payment")
    private String messageId;

    @Schema(description = "Related order identifier", example = "1")
    private Long orderId;
    @Schema(description = "Payment amount", example = "45000.00")
    private BigDecimal amount;
    @Schema(description = "Payment method", example = "CARD")
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Payment status", example = "PENDING")
    private PaymentStatus status;
}
