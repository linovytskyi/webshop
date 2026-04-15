package com.example.paymentservice.service;

import com.example.paymentservice.model.Payment;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with id " + id + " not found"));
    }

    public Payment create(Payment payment) {
        payment.setId(null);
        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.PENDING);
        }
        return paymentRepository.save(payment);
    }

    public Payment update(Long id, Payment updatedPayment) {
        Payment existingPayment = findById(id);
        existingPayment.setOrderId(updatedPayment.getOrderId());
        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
        existingPayment.setStatus(updatedPayment.getStatus());
        return paymentRepository.save(existingPayment);
    }

    public void delete(Long id) {
        Payment payment = findById(id);
        paymentRepository.delete(payment);
    }
}
