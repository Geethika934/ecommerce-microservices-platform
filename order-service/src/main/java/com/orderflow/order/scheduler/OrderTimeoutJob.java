package com.orderflow.order.scheduler;

import com.orderflow.order.entity.Order;
import com.orderflow.order.enums.OrderStatus;
import com.orderflow.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderTimeoutJob implements Job {

    private final OrderRepository orderRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Running order timeout job...");

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);

        List<Order> pendingOrders = orderRepository
                .findByStatusAndCreatedAtBefore(OrderStatus.PENDING, cutoffTime);

        if (pendingOrders.isEmpty()) {
            log.info("No pending orders to cancel.");
            return;
        }

        pendingOrders.forEach(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("Cancelled order: {} due to payment timeout", order.getId());
        });

        log.info("Order timeout job completed. Cancelled {} orders.", pendingOrders.size());
    }
}