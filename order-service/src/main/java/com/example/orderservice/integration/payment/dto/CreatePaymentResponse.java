package com.example.orderservice.integration.payment.dto;

import com.example.orderservice.model.payment.PaymentStatus;
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
@Schema(description = "Response returned by payment-service after payment creation")
public class CreatePaymentResponse {
    @Schema(description = "Payment identifier", example = "10")
    private Long id;
    @Schema(description = "Related order identifier", example = "1")
    private Long orderId;
    @Schema(description = "Payment amount", example = "45000.00")
    private BigDecimal amount;
    @Schema(description = "Payment method", example = "CARD")
    private String paymentMethod;
    @Schema(description = "Payment processing status", example = "PENDING")
    private PaymentStatus status;
}
