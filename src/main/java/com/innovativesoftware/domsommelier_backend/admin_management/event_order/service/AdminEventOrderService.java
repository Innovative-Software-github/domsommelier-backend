package com.innovativesoftware.domsommelier_backend.admin_management.event_order.service;

import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.AdminEventOrderDetailDto;
import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.AdminEventOrderListDto;
import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.EventOrderFilterRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.event_order.spec.EventOrderSpecifications;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.entity.EventOrder;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.enums.EventOrderStatus;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.repository.EventOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminEventOrderService {

    private final EventOrderRepository eventOrderRepository;

    @Transactional(readOnly = true)
    public Page<AdminEventOrderListDto> getEventOrders(EventOrderFilterRequest filter, Pageable pageable) {
        Specification<EventOrder> spec = EventOrderSpecifications.fromFilter(filter);
        return eventOrderRepository.findAll(spec, pageable).map(this::mapToListDto);
    }

    @Transactional(readOnly = true)
    public AdminEventOrderDetailDto getEventOrder(UUID id) {
        return mapToDetailDto(findById(id));
    }

    @Transactional
    public AdminEventOrderDetailDto updateStatus(UUID id, String newStatus) {
        EventOrder eventOrder = findById(id);

        EventOrderStatus status;
        try {
            status = EventOrderStatus.valueOf(newStatus);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неизвестный статус: " + newStatus);
        }

        eventOrder.setStatus(status);
        eventOrderRepository.save(eventOrder);
        return mapToDetailDto(eventOrder);
    }

    private EventOrder findById(UUID id) {
        return eventOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Заявка на мероприятие не найдена"));
    }

    private AdminEventOrderListDto mapToListDto(EventOrder eventOrder) {
        return AdminEventOrderListDto.builder()
                .id(eventOrder.getId())
                .createdAt(eventOrder.getCreatedAt())
                .name(eventOrder.getName())
                .phone(eventOrder.getPhone())
                .statusName(eventOrder.getStatus().name())
                .build();
    }

    private AdminEventOrderDetailDto mapToDetailDto(EventOrder eventOrder) {
        return AdminEventOrderDetailDto.builder()
                .id(eventOrder.getId())
                .createdAt(eventOrder.getCreatedAt())
                .name(eventOrder.getName())
                .phone(eventOrder.getPhone())
                .comment(eventOrder.getComment())
                .statusName(eventOrder.getStatus().name())
                .build();
    }
}
