package com.orderflow.order.service.impl;

import com.orderflow.common.event.OrderPlacedEvent;
import com.orderflow.order.dto.request.PlaceOrderRequest;
import com.orderflow.order.dto.response.OrderResponse;
import com.orderflow.order.entity.Order;
import com.orderflow.order.enums.OrderStatus;
import com.orderflow.order.kafka.OrderEventProducer;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request, String customerId, String customerEmail) {
        Order order = Order.builder()
                .customerId(customerId)
                .customerEmail(customerEmail)
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .totalAmount(request.getTotalAmount())
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {}", savedOrder.getId());

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(savedOrder.getId())
                .customerId(customerId)
                .customerEmail(customerEmail)
                .productId(savedOrder.getProductId())
                .productName(savedOrder.getProductName())
                .quantity(savedOrder.getQuantity())
                .totalAmount(savedOrder.getTotalAmount())
                .shippingAddress(savedOrder.getShippingAddress())
                .createdAt(savedOrder.getCreatedAt())
                .build();

        orderEventProducer.publishOrderPlaced(event);
        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(String customerEmail) {
        return orderRepository.findByCustomerEmail(customerEmail)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(String orderId, String customerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomerEmail().equals(customerEmail)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .customerEmail(order.getCustomerEmail())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}