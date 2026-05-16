package com.example.orderservice.messaging.listener;

import com.example.orderservice.messaging.config.RabbitMQConfig;
import com.example.orderservice.messaging.dto.PaymentResultMessage;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResultListener {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_RESULTS_QUEUE)
    public void handlePaymentResult(PaymentResultMessage message) {
        log.info("Received payment result for orderId={}, status={}", message.getOrderId(), message.getStatus());
        orderService.updatePaymentStatus(message.getOrderId(), message.getStatus());
    }
}