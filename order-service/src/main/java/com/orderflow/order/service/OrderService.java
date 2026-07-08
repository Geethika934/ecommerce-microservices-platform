package com.orderflow.order.service;

import com.orderflow.order.dto.request.PlaceOrderRequest;
import com.orderflow.order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(PlaceOrderRequest request, String customerId, String customerEmail);
    List<OrderResponse> getMyOrders(String customerEmail);
    OrderResponse getOrderById(String orderId, String customerEmail);
}