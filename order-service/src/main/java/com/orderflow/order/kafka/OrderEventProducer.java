package com.orderflow.order.kafka;

import com.orderflow.common.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public static final String ORDER_PLACED_TOPIC = "order.placed";

    public void publishOrderPlaced(OrderPlacedEvent event) {
        kafkaTemplate.send(ORDER_PLACED_TOPIC, event.getOrderId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish OrderPlacedEvent for orderId: {}",
                                event.getOrderId(), ex);
                    } else {
                        log.info("OrderPlacedEvent published for orderId: {} to topic: {}",
                                event.getOrderId(), ORDER_PLACED_TOPIC);
                    }
                });
    }
}
