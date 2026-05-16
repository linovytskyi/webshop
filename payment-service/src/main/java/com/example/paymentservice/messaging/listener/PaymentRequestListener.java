package com.example.paymentservice.messaging.listener;

import com.example.paymentservice.messaging.config.RabbitMQConfig;
import com.example.paymentservice.messaging.dto.PaymentRequestMessage;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_REQUESTS_QUEUE)
    public void handlePaymentRequest(PaymentRequestMessage message) {
        log.info("Received payment request for orderId={}, messageId={}", message.getOrderId(), message.getMessageId());
        paymentService.createFromMessage(message);
    }
}