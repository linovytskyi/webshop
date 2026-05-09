package com.example.deliveryservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deliveries")
@Schema(description = "Delivery entity stored in delivery-service")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Delivery identifier", example = "5")
    private Long id;

    @Schema(description = "Related order identifier", example = "1")
    private Long orderId;
    @Schema(description = "Delivery address", example = "Kyiv, Khreshchatyk 1")
    private String address;
    @Schema(description = "Planned delivery date", example = "2026-04-20")
    private LocalDate deliveryDate;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Delivery status", example = "IN_PROGRESS")
    private DeliveryStatus status;
}
