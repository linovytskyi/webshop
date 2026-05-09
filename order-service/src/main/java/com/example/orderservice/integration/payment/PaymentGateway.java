package com.example.orderservice.integration.payment;

import com.example.orderservice.integration.payment.client.PaymentServiceClient;
import com.example.orderservice.integration.payment.dto.CreatePaymentRequest;
import com.example.orderservice.integration.payment.dto.CreatePaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentServiceClient paymentServiceClient;

    @CircuitBreaker(name = "paymentClient")
    public CreatePaymentResponse createPayment(String idempotencyKey, CreatePaymentRequest request) {
        return paymentServiceClient.createPayment(idempotencyKey, request);
    }
}
