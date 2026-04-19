package com.example.orderservice.integration.payment.client;

import com.example.orderservice.integration.payment.dto.CreatePaymentRequest;
import com.example.orderservice.integration.payment.dto.CreatePaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-service-client", url = "${integration.payment.url}", path = "/api/payments")
public interface PaymentServiceClient {

    @PostMapping
    CreatePaymentResponse createPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreatePaymentRequest request
    );
}
