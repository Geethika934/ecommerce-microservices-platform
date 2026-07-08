package com.orderflow.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Double price;
    @NotNull
    private Integer stock;
    @NotBlank
    private String category;
    @NotBlank
    private String brand;
    private String imageUrl;
}