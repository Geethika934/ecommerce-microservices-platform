package com.orderflow.order.repository;

import com.orderflow.order.entity.Order;
import com.orderflow.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCustomerId(String customerId);
    List<Order> findByCustomerEmail(String customerEmail);
    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime cutoffTime);
}