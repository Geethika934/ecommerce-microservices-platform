package com.orderflow.order.dto.response;

import com.orderflow.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private String id;
    private String customerId;
    private String customerEmail;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double totalAmount;
    private String shippingAddress;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
