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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
    }

    public Order create(Order order) {
        order.setId(null);
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.CREATED);
        }
        return orderRepository.save(order);
    }

    public Order update(Long id, Order updatedOrder) {
        Order existingOrder = findById(id);
        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setProductName(updatedOrder.getProductName());
        existingOrder.setQuantity(updatedOrder.getQuantity());
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());
        existingOrder.setStatus(updatedOrder.getStatus());
        return orderRepository.save(existingOrder);
    }

    public void delete(Long id) {
        Order order = findById(id);
        orderRepository.delete(order);
    }

    public CreatePaymentResponse requestPayment(Long orderId, String paymentMethod) {
        Order order = findById(orderId);

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(order.getId())
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .build();

        CreatePaymentResponse paymentResponse = paymentServiceClient.createPayment(request);

        if (paymentResponse.getStatus() == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.PAYMENT_REQUESTED);
        }
        orderRepository.save(order);

        return paymentResponse;
    }
}
