package com.testtask.services;

import com.testtask.entity.Order;
import com.testtask.repo.OrderItemRepository;
import com.testtask.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public void deleteUnpaidOrders() {
        LocalDateTime currentTime = LocalDateTime.now();

        List<Order> unpaidOrders = orderRepository.findUnpaidOrdersOlderThan(currentTime.minusMinutes(10));

        for (Order order : unpaidOrders) {
            deleteOrderWithItems(order.getId());
        }
    }

    @Transactional
    public void deleteOrderWithItems(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            orderItemRepository.deleteByOrder(order);
            orderRepository.delete(order);
        }
    }

    @Transactional
    public boolean markOrderAsPaid(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setPaid(true);
            orderRepository.save(order);

            return true;
        }

        return false;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
}
