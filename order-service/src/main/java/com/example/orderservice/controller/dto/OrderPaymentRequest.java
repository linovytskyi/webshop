package com.example.orderservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for sending an order to payment-service")
public class OrderPaymentRequest {

    @Schema(description = "Selected payment method", example = "CARD")
    private String paymentMethod;
}
