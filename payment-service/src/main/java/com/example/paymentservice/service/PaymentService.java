package com.example.paymentservice.service;

import com.example.paymentservice.messaging.dto.PaymentRequestMessage;
import com.example.paymentservice.messaging.dto.PaymentResultMessage;
import com.example.paymentservice.messaging.publisher.PaymentResultPublisher;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.model.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentResultPublisher paymentResultPublisher;

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

        if (isTerminalStatus(savedPayment.getStatus()) && savedPayment.getMessageId() != null) {
            publishResult(savedPayment);
        }

        return savedPayment;
    }

    public void delete(Long id) {
        log.info("Deleting payment id={}", id);
        Payment payment = findById(id);
        paymentRepository.delete(payment);
        log.info("Payment id={} deleted", id);
    }

    @Transactional
    public Payment createFromMessage(PaymentRequestMessage message) {
        log.info("Processing payment message for orderId={}, messageId={}", message.getOrderId(), message.getMessageId());
        return paymentRepository.findByMessageId(message.getMessageId())
                .orElseGet(() -> savePaymentFromMessage(message));
    }

    private Payment savePaymentFromMessage(PaymentRequestMessage message) {
        Payment payment = Payment.builder()
                .messageId(message.getMessageId())
                .orderId(message.getOrderId())
                .amount(message.getAmount())
                .paymentMethod(message.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();
        Payment saved = paymentRepository.save(payment);
        log.info("Payment created with id={} for orderId={}", saved.getId(), saved.getOrderId());
        return saved;
    }

    private boolean isTerminalStatus(PaymentStatus status) {
        return status == PaymentStatus.COMPLETED || status == PaymentStatus.FAILED;
    }

    private void publishResult(Payment payment) {
        PaymentResultMessage result = PaymentResultMessage.builder()
                .messageId(payment.getMessageId())
                .orderId(payment.getOrderId())
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .build();
        paymentResultPublisher.publish(result);
        log.info("Payment result published for orderId={}, status={}", payment.getOrderId(), payment.getStatus());
    }
}
