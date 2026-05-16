package com.example.deliveryservice.kafka.publisher;

import com.example.deliveryservice.kafka.config.KafkaConfig;
import com.example.deliveryservice.kafka.dto.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCreatedPublisher {

    private final KafkaTemplate<String, DeliveryCreatedEvent> kafkaTemplate;

    public void publish(DeliveryCreatedEvent event) {
        kafkaTemplate.send(KafkaConfig.DELIVERY_CREATED_TOPIC, String.valueOf(event.getOrderId()), event);
        log.info("Published DeliveryCreatedEvent for orderId={}, deliveryId={}", event.getOrderId(), event.getDeliveryId());
    }
}