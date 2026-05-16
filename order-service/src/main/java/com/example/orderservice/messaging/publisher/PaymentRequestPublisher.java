package com.example.orderservice.messaging.publisher;

import com.example.orderservice.messaging.config.RabbitMQConfig;
import com.example.orderservice.messaging.dto.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(PaymentRequestMessage message) {
        log.info("Publishing payment request for orderId={}, messageId={}", message.getOrderId(), message.getMessageId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ROUTING_KEY_PAYMENT_REQUEST, message);
    }
}