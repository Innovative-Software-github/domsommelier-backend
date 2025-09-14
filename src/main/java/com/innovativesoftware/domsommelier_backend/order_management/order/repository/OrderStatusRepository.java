package com.innovativesoftware.domsommelier_backend.order_management.order.repository;

import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> { }
