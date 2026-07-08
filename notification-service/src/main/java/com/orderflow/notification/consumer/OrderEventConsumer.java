package com.orderflow.notification.consumer;

import com.orderflow.common.event.OrderPlacedEvent;
import com.orderflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "order.placed",
            groupId = "notification-group"
    )
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for orderId: {}", event.getOrderId());
        notificationService.sendOrderConfirmation(event);
    }
}