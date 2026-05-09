package com.example.orderservice.controller;

import com.example.orderservice.controller.dto.ErrorResponse;
import com.example.orderservice.controller.dto.OrderPaymentRequest;
import com.example.orderservice.integration.payment.dto.CreatePaymentResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Operations for managing orders and requesting payment")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns the full list of orders from the database")
    @ApiResponse(responseCode = "200", description = "Orders returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class))))
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("Received request to get all orders");
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id", description = "Returns a single order by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("Received request to get order by id={}", id);
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Creates a new order record")
    @ApiResponse(responseCode = "201", description = "Order created",
            content = @Content(schema = @Schema(implementation = Order.class)))
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.info("Received request to create order for customer='{}'", order.getCustomerName());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(order));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Updates an existing order by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated",
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        log.info("Received request to update order id={}", id);
        return ResponseEntity.ok(orderService.update(id, order));
    }

    @PostMapping("/{id}/payment")
    @Operation(summary = "Request payment for order",
            description = "Sends a REST request from order-service to payment-service through OpenFeign")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment request sent successfully",
                    content = @Content(schema = @Schema(implementation = CreatePaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CreatePaymentResponse> requestPayment(
            @PathVariable Long id,
            @RequestBody OrderPaymentRequest request
    ) {
        log.info("Received payment request for order id={} using method={}", id, request.getPaymentMethod());
        return ResponseEntity.ok(orderService.requestPayment(id, request.getPaymentMethod()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Deletes an order by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("Received request to delete order id={}", id);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
