package com.example.orderservice.kafka.listener;

import com.example.orderservice.kafka.config.KafkaConfig;
import com.example.orderservice.kafka.dto.DeliveryCreatedEvent;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCreatedListener {

    private final OrderService orderService;

    @KafkaListener(topics = KafkaConfig.DELIVERY_CREATED_TOPIC, groupId = KafkaConfig.ORDER_SERVICE_GROUP_ID)
    public void handleDeliveryCreated(DeliveryCreatedEvent event) {
        log.info("Received DeliveryCreatedEvent for orderId={}, deliveryId={}", event.getOrderId(), event.getDeliveryId());
        orderService.updateDeliveryStatus(event.getOrderId());
    }
}