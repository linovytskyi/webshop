package com.example.paymentservice.controller;

import com.example.paymentservice.controller.dto.ErrorResponse;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Operations for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Get all payments", description = "Returns the full list of payments")
    @ApiResponse(responseCode = "200", description = "Payments returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Payment.class))))
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by id", description = "Returns a payment by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create payment", description = "Creates a new payment record")
    @ApiResponse(responseCode = "201", description = "Payment created",
            content = @Content(schema = @Schema(implementation = Payment.class)))
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(payment));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment", description = "Updates an existing payment by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.update(id, payment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Deletes a payment by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
