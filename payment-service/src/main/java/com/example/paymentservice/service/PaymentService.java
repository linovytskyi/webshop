package com.example.paymentservice.service;

import com.example.paymentservice.model.Payment;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<Payment> findAll() {
        log.info("Fetching all payments");
        return paymentRepository.findAll();
    }

    public Payment findById(Long id) {
        log.info("Fetching payment by id={}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with id " + id + " not found"));
    }

    public Payment create(Payment payment) {
        log.info("Creating new payment for orderId={}", payment.getOrderId());
        payment.setId(null);
        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with id={} and status={}", savedPayment.getId(), savedPayment.getStatus());
        return savedPayment;
    }

    public Payment update(Long id, Payment updatedPayment) {
        log.info("Updating payment id={}", id);
        Payment existingPayment = findById(id);
        existingPayment.setOrderId(updatedPayment.getOrderId());
        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
        existingPayment.setStatus(updatedPayment.getStatus());
        Payment savedPayment = paymentRepository.save(existingPayment);
        log.info("Payment id={} updated with status={}", savedPayment.getId(), savedPayment.getStatus());
        return savedPayment;
    }

    public void delete(Long id) {
        log.info("Deleting payment id={}", id);
        Payment payment = findById(id);
        paymentRepository.delete(payment);
        log.info("Payment id={} deleted", id);
    }
}
