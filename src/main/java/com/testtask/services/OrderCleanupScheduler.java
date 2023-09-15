package com.testtask.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderCleanupScheduler {

    private final OrderService orderService;

    @Autowired
    public OrderCleanupScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 600000)
    public void cleanupUnpaidOrders() {
        orderService.deleteUnpaidOrders();
    }
}