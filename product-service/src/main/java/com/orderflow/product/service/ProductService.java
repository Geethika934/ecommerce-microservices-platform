package com.orderflow.product.service;

import com.orderflow.product.dto.request.CreateProductRequest;
import com.orderflow.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProductById(String id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(String category);
    ProductResponse updateStock(String productId, Integer quantity);
}