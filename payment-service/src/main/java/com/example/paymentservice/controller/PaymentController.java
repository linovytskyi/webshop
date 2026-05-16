package com.example.paymentservice.controller;

import com.example.paymentservice.controller.dto.ErrorResponse;
import com.example.paymentservice.idempotency.IdempotencyConstants;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Operations for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @CircuitBreaker(name = "serverController")
    @RateLimiter(name = "serverController")
    @Operation(summary = "Get all payments", description = "Returns the full list of payments")
    @ApiResponse(responseCode = "200", description = "Payments returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Payment.class))))
    public ResponseEntity<List<Payment>> getAllPayments() {
        log.info("Received request to get all payments");
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    @CircuitBreaker(name = "serverController")
    @RateLimiter(name = "serverController")
    @Operation(summary = "Get payment by id", description = "Returns a payment by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        log.info("Received request to get payment by id={}", id);
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping
    @CircuitBreaker(name = "serverController")
    @RateLimiter(name = "serverController")
    @Operation(summary = "Create payment",
            description = "Creates a new payment record. Requires the Idempotency-Key header to prevent duplicate charges. "
                    + "If the same key is reused after completion, the cached response is returned. "
                    + "If the same key is reused while the first request is still processing, the API returns 409 Conflict.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment created",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "400", description = "Idempotency-Key header is missing",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Request with the same idempotency key is already being processed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Idempotency key was reused with a different request body or endpoint",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Payment> createPayment(
            @Parameter(
                    description = "Unique key for safe request retries. Required for idempotent payment creation.",
                    required = true,
                    example = "payment-order-1"
            )
            @RequestHeader(IdempotencyConstants.IDEMPOTENCY_KEY_HEADER) String idempotencyKey,
            @RequestBody Payment payment
    ) {
        log.info("Received request to create payment for orderId={} with idempotencyKey={}",
                payment.getOrderId(), idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(payment));
    }

    @PutMapping("/{id}")
    @CircuitBreaker(name = "serverController")
    @RateLimiter(name = "serverController")
    @Operation(summary = "Update payment", description = "Updates an existing payment by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        log.info("Received request to update payment id={}", id);
        return ResponseEntity.ok(paymentService.update(id, payment));
    }

    @DeleteMapping("/{id}")
    @CircuitBreaker(name = "serverController")
    @RateLimiter(name = "serverController")
    @Operation(summary = "Delete payment", description = "Deletes a payment by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.info("Received request to delete payment id={}", id);
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
