package com.example.orderservice.kafka.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaidEvent {
    private Long orderId;
    private String customerName;
    private String productName;
    private BigDecimal totalPrice;
    private String paymentMethod;
}