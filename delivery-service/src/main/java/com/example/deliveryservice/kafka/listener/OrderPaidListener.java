package com.example.deliveryservice.kafka.listener;

import com.example.deliveryservice.kafka.config.KafkaConfig;
import com.example.deliveryservice.kafka.dto.OrderPaidEvent;
import com.example.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaidListener {

    private final DeliveryService deliveryService;

    @KafkaListener(topics = KafkaConfig.ORDER_PAID_TOPIC, groupId = KafkaConfig.DELIVERY_SERVICE_GROUP_ID)
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("Received OrderPaidEvent for orderId={}", event.getOrderId());
        deliveryService.createFromEvent(event);
    }
}