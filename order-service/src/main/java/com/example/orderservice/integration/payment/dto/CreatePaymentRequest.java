package com.example.orderservice.integration.payment.dto;

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
@Schema(description = "Request sent from order-service to payment-service")
public class CreatePaymentRequest {
    @Schema(description = "Related order identifier", example = "1")
    private Long orderId;
    @Schema(description = "Amount to be paid", example = "45000.00")
    private BigDecimal amount;
    @Schema(description = "Requested payment method", example = "CARD")
    private String paymentMethod;
}
