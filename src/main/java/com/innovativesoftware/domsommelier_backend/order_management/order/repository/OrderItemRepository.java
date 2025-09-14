package com.innovativesoftware.domsommelier_backend.order_management.order.repository;

import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> { }
