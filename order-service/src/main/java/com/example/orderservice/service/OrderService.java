package com.example.orderservice.service;

import com.example.orderservice.messaging.dto.PaymentRequestMessage;
import com.example.orderservice.messaging.publisher.PaymentRequestPublisher;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
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
    private final PaymentRequestPublisher paymentRequestPublisher;

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

    public Order requestPayment(Long orderId, String paymentMethod) {
        log.info("Requesting payment for order id={} using method={}", orderId, paymentMethod);
        Order order = findById(orderId);

        if (order.getStatus() == OrderStatus.PAYMENT_REQUESTED || order.getStatus() == OrderStatus.PAID) {
            log.warn("Payment already in progress or completed for order id={}, status={}", orderId, order.getStatus());
            return order;
        }

        PaymentRequestMessage message = PaymentRequestMessage.builder()
                .messageId("payment-order-" + orderId)
                .orderId(order.getId())
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .build();

        order.setStatus(OrderStatus.PAYMENT_REQUESTED);
        orderRepository.save(order);

        paymentRequestPublisher.publish(message);
        log.info("Payment request published for order id={}, messageId={}", orderId, message.getMessageId());

        return order;
    }

    public void updatePaymentStatus(Long orderId, String paymentStatus) {
        log.info("Updating payment status for order id={}, paymentStatus={}", orderId, paymentStatus);
        Order order = findById(orderId);
        OrderStatus newStatus = "COMPLETED".equals(paymentStatus) ? OrderStatus.PAID : OrderStatus.CANCELLED;
        order.setStatus(newStatus);
        orderRepository.save(order);
        log.info("Order id={} status updated to {}", orderId, newStatus);
    }
}