package com.orderflow.product.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private String brand;
    private String imageUrl;
    private boolean active;
    private LocalDateTime createdAt;
}