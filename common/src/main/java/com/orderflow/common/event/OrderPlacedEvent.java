package com.orderflow.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {
    private String orderId;
    private String customerId;
    private String customerEmail;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double totalAmount;
    private String shippingAddress;
    private LocalDateTime createdAt;
}
