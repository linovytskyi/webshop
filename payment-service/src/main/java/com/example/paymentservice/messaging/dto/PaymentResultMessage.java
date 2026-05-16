package com.example.paymentservice.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultMessage {
    private String messageId;
    private Long orderId;
    private Long paymentId;
    private String status;
}