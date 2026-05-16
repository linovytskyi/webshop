package com.example.orderservice.integration.payment;

import com.example.orderservice.integration.payment.client.PaymentServiceClient;
import com.example.orderservice.integration.payment.dto.CreatePaymentRequest;
import com.example.orderservice.integration.payment.dto.CreatePaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentGateway {

    private final PaymentServiceClient paymentServiceClient;

    @CircuitBreaker(name = "paymentClient")
    @Retry(name = "paymentClient")
    public CreatePaymentResponse createPayment(String idempotencyKey, CreatePaymentRequest request) {
        log.info("Calling payment-service for orderId={}, idempotencyKey={}",
                request.getOrderId(), idempotencyKey);
        return paymentServiceClient.createPayment(idempotencyKey, request);
    }
}
