package com.orderflow.order.controller;

import com.orderflow.order.dto.request.PlaceOrderRequest;
import com.orderflow.order.dto.response.OrderResponse;
import com.orderflow.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            Authentication authentication) {

        String customerEmail = authentication.getName();
        String customerId = authentication.getName(); // using email as id for now

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(request, customerId, customerEmail));
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrders(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable String id,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(id, authentication.getName()));
    }
}