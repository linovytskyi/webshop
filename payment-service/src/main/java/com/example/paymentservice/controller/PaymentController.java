package com.example.paymentservice.controller;

import com.example.paymentservice.model.Payment;
import com.example.paymentservice.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentService.create(payment);
    }

    @PutMapping("/{id}")
    public Payment updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        return paymentService.update(id, payment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long id) {
        paymentService.delete(id);
    }
}
