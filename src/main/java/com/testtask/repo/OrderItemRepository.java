package com.testtask.repo;

import com.testtask.entity.Order;
import com.testtask.entity.OrderItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.order = :order")
    void deleteByOrder(@Param("order") Order order);
}
