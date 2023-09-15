package com.testtask.controller;

import com.testtask.entity.Order;
import com.testtask.entity.OrderItem;
import com.testtask.entity.Product;
import com.testtask.services.OrderItemService;
import com.testtask.services.OrderService;
import com.testtask.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;
    private final ProductService productService;

    private final OrderItemService orderItemService;

    @Autowired
    public OrderRestController(OrderService orderService, ProductService productService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.productService = productService;
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public Order placeOrder(@RequestBody List<OrderItem> orderItems) {
        Order order = new Order();
        order.setPaid(false);
        order.setCreatedTime(LocalDateTime.now());
        orderService.save(order);

        for (OrderItem orderItem : orderItems) {
            Product product = productService.findById(orderItem.getProduct().getId());

            if (product != null && product.getQuantity() >= orderItem.getQuantity()) {
                orderItem.setOrder(order);
                orderItem.setProduct(product);

                orderItemService.save(orderItem);

                int remainingQuantity = product.getQuantity() - orderItem.getQuantity();
                product.setQuantity(remainingQuantity);
                productService.save(product);
            }
        }

        return order;
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<String> markOrderAsPaid(@PathVariable Long orderId) {
        boolean paymentSuccessful = orderService.markOrderAsPaid(orderId);

        if (paymentSuccessful) {
            return ResponseEntity.ok("Order has been successfully paid.");
        } else {
            return ResponseEntity.badRequest().body("Payment failed. Please try again.");
        }
    }
}
