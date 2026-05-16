package com.example.paymentservice.messaging.publisher;

import com.example.paymentservice.messaging.config.RabbitMQConfig;
import com.example.paymentservice.messaging.dto.PaymentResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResultPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(PaymentResultMessage message) {
        String routingKey = "payment." + message.getStatus().toLowerCase();
        log.info("Publishing payment result for orderId={}, status={}, routingKey={}", message.getOrderId(), message.getStatus(), routingKey);
        rabbitTemplate.convertAndSend(RabbitMQConfig.PAYMENT_EXCHANGE, routingKey, message);
    }
}