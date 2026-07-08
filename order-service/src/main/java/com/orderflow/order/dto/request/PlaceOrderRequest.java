package com.orderflow.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotBlank
    private String productId;
    @NotBlank
    private String productName;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double totalAmount;
    @NotBlank
    private String shippingAddress;
}