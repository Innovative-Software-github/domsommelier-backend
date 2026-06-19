package com.innovativesoftware.domsommelier_backend.order_management.order.repository;

import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    List<Order> findAllByCustomerId(UUID customerId);
    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Order> findAllByCustomerId(UUID customerId, Pageable pageable);
}
