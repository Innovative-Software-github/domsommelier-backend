package com.innovativesoftware.domsommelier_backend.admin_management.event_order.controller;

import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.AdminEventOrderDetailDto;
import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.AdminEventOrderListDto;
import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.EventOrderFilterRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.UpdateEventOrderStatusRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.event_order.service.AdminEventOrderService;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiresAdmin
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/event-orders")
@Tag(name = "Admin Event Orders", description = "Заявки на проведение мероприятий (admin)")
public class AdminEventOrderController {

    private final AdminEventOrderService adminEventOrderService;

    @GetMapping
    @Operation(summary = "Список заявок на мероприятия с фильтрами")
    public ResponseEntity<Page<AdminEventOrderListDto>> getEventOrders(
            @ModelAttribute EventOrderFilterRequest filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(adminEventOrderService.getEventOrders(filter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Детали заявки на мероприятие")
    public ResponseEntity<AdminEventOrderDetailDto> getEventOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(adminEventOrderService.getEventOrder(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Сменить статус заявки")
    public ResponseEntity<AdminEventOrderDetailDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEventOrderStatusRequest request
    ) {
        return ResponseEntity.ok(adminEventOrderService.updateStatus(id, request.getStatus()));
    }
}
