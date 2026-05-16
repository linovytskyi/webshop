package com.example.deliveryservice.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCreatedEvent {
    private Long deliveryId;
    private Long orderId;
    private String status;
}