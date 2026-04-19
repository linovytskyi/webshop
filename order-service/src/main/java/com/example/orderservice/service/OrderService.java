package com.example.orderservice.service;

import com.example.orderservice.integration.payment.client.PaymentServiceClient;
import com.example.orderservice.integration.payment.dto.CreatePaymentRequest;
import com.example.orderservice.integration.payment.dto.CreatePaymentResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.model.payment.PaymentStatus;
import com.example.orderservice.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;

    public List<Order> findAll() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        log.info("Fetching order by id={}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    public Order create(Order order) {
        log.info("Creating new order for customer='{}'", order.getCustomerName());
        order.setId(null);
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.CREATED);
        }
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id={} and status={}", savedOrder.getId(), savedOrder.getStatus());
        return savedOrder;
    }

    public Order update(Long id, Order updatedOrder) {
        log.info("Updating order id={}", id);
        Order existingOrder = findById(id);
        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setProductName(updatedOrder.getProductName());
        existingOrder.setQuantity(updatedOrder.getQuantity());
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());
        existingOrder.setStatus(updatedOrder.getStatus());
        Order savedOrder = orderRepository.save(existingOrder);
        log.info("Order id={} updated with status={}", savedOrder.getId(), savedOrder.getStatus());
        return savedOrder;
    }

    public void delete(Long id) {
        log.info("Deleting order id={}", id);
        Order order = findById(id);
        orderRepository.delete(order);
        log.info("Order id={} deleted", id);
    }

    public CreatePaymentResponse requestPayment(Long orderId, String paymentMethod) {
        log.info("Requesting payment for order id={} using method={}", orderId, paymentMethod);
        Order order = findById(orderId);
        String idempotencyKey = buildPaymentIdempotencyKey(order.getId());

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(order.getId())
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .build();

        log.debug("Sending payment request for order id={} with idempotencyKey={}", order.getId(), idempotencyKey);
        CreatePaymentResponse paymentResponse = paymentServiceClient.createPayment(idempotencyKey, request);

        if (paymentResponse.getStatus() == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.PAYMENT_REQUESTED);
        }
        orderRepository.save(order);
        log.info("Payment requested for order id={}, paymentStatus={}, orderStatus={}",
                orderId, paymentResponse.getStatus(), order.getStatus());

        return paymentResponse;
    }

    private String buildPaymentIdempotencyKey(Long orderId) {
        return "payment-order-" + orderId;
    }
}
