package com.innovativesoftware.domsommelier_backend.event_management.event_order.service;

import com.innovativesoftware.domsommelier_backend.event_management.event_order.entity.EventOrder;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.enums.EventOrderStatus;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.model.CreateEventOrderRequest;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.repository.EventOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventOrderService {

    private final EventOrderRepository eventOrderRepository;

    @Transactional
    public UUID create(CreateEventOrderRequest request) {
        EventOrder eventOrder = EventOrder.builder()
                .name(request.getName().trim())
                .phone(request.getPhone().trim())
                .comment(request.getComment() != null ? request.getComment().trim() : null)
                .status(EventOrderStatus.NEW)
                .build();

        return eventOrderRepository.save(eventOrder).getId();
    }
}
