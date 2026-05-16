package com.example.orderservice.kafka.publisher;

import com.example.orderservice.kafka.config.KafkaConfig;
import com.example.orderservice.kafka.dto.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaidPublisher {

    private final KafkaTemplate<String, OrderPaidEvent> kafkaTemplate;

    public void publish(OrderPaidEvent event) {
        kafkaTemplate.send(KafkaConfig.ORDER_PAID_TOPIC, String.valueOf(event.getOrderId()), event);
        log.info("Published OrderPaidEvent for orderId={}", event.getOrderId());
    }
}