package com.innovativesoftware.domsommelier_backend.order_management.order.repository;

import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByCustomerId(UUID customerId);
}
