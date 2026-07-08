package com.orderflow.product.consumer;

import com.orderflow.common.event.OrderPlacedEvent;
import com.orderflow.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ProductService productService;

    @KafkaListener(
        topics = "order.placed",
        groupId = "product-group"
    )
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent in product-service for orderId: {}", 
            event.getOrderId());
        productService.updateStock(event.getProductId(), event.getQuantity());
    }
}