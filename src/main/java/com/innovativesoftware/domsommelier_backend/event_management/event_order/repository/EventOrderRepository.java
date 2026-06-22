package com.innovativesoftware.domsommelier_backend.event_management.event_order.repository;

import com.innovativesoftware.domsommelier_backend.event_management.event_order.entity.EventOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface EventOrderRepository
        extends JpaRepository<EventOrder, UUID>, JpaSpecificationExecutor<EventOrder> {
}
