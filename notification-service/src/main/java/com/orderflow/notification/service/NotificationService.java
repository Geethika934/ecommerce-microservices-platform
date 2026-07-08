package com.orderflow.notification.service;

import com.orderflow.common.event.OrderPlacedEvent;

public interface NotificationService {
    void sendOrderConfirmation(OrderPlacedEvent event);
}