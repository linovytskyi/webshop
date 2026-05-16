package com.example.orderservice.messaging.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestMessage {
    private String messageId;
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
}