package com.example.orderservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@Schema(description = "Order entity stored in order-service")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Order identifier", example = "1")
    private Long id;

    @Schema(description = "Customer full name", example = "Ivan Petrenko")
    private String customerName;
    @Schema(description = "Ordered product name", example = "Laptop")
    private String productName;
    @Schema(description = "Number of items in the order", example = "2")
    private Integer quantity;
    @Schema(description = "Total order price", example = "90000.00")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Current order status", example = "PAYMENT_REQUESTED")
    private OrderStatus status;
}
