package com.orderflow.product.repository;

import com.orderflow.product.document.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findByBrand(String brand);
    List<Product> findByActiveTrue();
    Optional<Product> findByIdAndActiveTrue(String id);
    List<Product> findByNameContainingIgnoreCase(String name);
}