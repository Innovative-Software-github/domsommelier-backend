package com.innovativesoftware.domsommelier_backend.admin_management.order.controller;

import com.innovativesoftware.domsommelier_backend.admin_management.order.model.AdminOrderDetailDto;
import com.innovativesoftware.domsommelier_backend.admin_management.order.model.AdminOrderFilterRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.order.model.AdminOrderListDto;
import com.innovativesoftware.domsommelier_backend.admin_management.order.model.UpdateOrderStatusRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.order.service.AdminOrderService;
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

import java.util.Map;
import java.util.UUID;

@RestController
@RequiresAdmin
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/orders")
@Tag(name = "Admin Orders", description = "Управление заказами (admin)")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    @Operation(summary = "Список всех заказов с фильтрами")
    public ResponseEntity<Page<AdminOrderListDto>> getOrders(
            @ModelAttribute AdminOrderFilterRequest filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(adminOrderService.getOrders(filter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Детали заказа")
    public ResponseEntity<AdminOrderDetailDto> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(adminOrderService.getOrder(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Сменить статус заказа")
    public ResponseEntity<AdminOrderDetailDto> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(adminOrderService.updateStatus(id, request.getStatus()));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Отменить заказ")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable UUID id) {
        AdminOrderDetailDto order = adminOrderService.cancelOrder(id);
        return ResponseEntity.ok(Map.of("status", order.getStatusName()));
    }
}
