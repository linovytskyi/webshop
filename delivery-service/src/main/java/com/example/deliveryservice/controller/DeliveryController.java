package com.example.deliveryservice.controller;

import com.example.deliveryservice.controller.dto.ErrorResponse;
import com.example.deliveryservice.model.Delivery;
import com.example.deliveryservice.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deliveries", description = "Operations for managing deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    @Operation(summary = "Get all deliveries", description = "Returns the full list of deliveries")
    @ApiResponse(responseCode = "200", description = "Deliveries returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Delivery.class))))
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        log.info("Received request to get all deliveries");
        return ResponseEntity.ok(deliveryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get delivery by id", description = "Returns a delivery by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery found",
                    content = @Content(schema = @Schema(implementation = Delivery.class))),
            @ApiResponse(responseCode = "404", description = "Delivery not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        log.info("Received request to get delivery by id={}", id);
        return ResponseEntity.ok(deliveryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create delivery", description = "Creates a new delivery record")
    @ApiResponse(responseCode = "201", description = "Delivery created",
            content = @Content(schema = @Schema(implementation = Delivery.class)))
    public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery) {
        log.info("Received request to create delivery for orderId={}", delivery.getOrderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.create(delivery));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update delivery", description = "Updates an existing delivery by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery updated",
                    content = @Content(schema = @Schema(implementation = Delivery.class))),
            @ApiResponse(responseCode = "404", description = "Delivery not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Delivery> updateDelivery(@PathVariable Long id, @RequestBody Delivery delivery) {
        log.info("Received request to update delivery id={}", id);
        return ResponseEntity.ok(deliveryService.update(id, delivery));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete delivery", description = "Deletes a delivery by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Delivery deleted"),
            @ApiResponse(responseCode = "404", description = "Delivery not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        log.info("Received request to delete delivery id={}", id);
        deliveryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
