package com.orderflow.notification.service.impl;

import com.orderflow.common.event.OrderPlacedEvent;
import com.orderflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOrderConfirmation(OrderPlacedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(event.getCustomerEmail());
            message.setSubject("Order Confirmation - " + event.getOrderId());
            message.setText(buildEmailBody(event));
            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", event.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send email for orderId: {}", event.getOrderId(), e);
        }
    }

    private String buildEmailBody(OrderPlacedEvent event) {
        return """
                Dear Customer,
                
                Your order has been placed successfully!
                
                Order Details:
                - Order ID: %s
                - Product: %s
                - Quantity: %d
                - Total Amount: ₹%.2f
                - Shipping Address: %s
                
                Thank you for shopping with OrderFlow!
                """.formatted(
                event.getOrderId(),
                event.getProductName(),
                event.getQuantity(),
                event.getTotalAmount(),
                event.getShippingAddress()
        );
    }
}