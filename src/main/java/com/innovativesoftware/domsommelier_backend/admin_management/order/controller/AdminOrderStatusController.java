package com.innovativesoftware.domsommelier_backend.admin_management.order.controller;

import com.innovativesoftware.domsommelier_backend.admin_management.order.model.OrderStatusOptionDto;
import com.innovativesoftware.domsommelier_backend.admin_management.order.service.AdminOrderService;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiresAdmin
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/order-statuses")
@Tag(name = "Admin Order Statuses", description = "Справочник статусов заказов (admin)")
public class AdminOrderStatusController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    @Operation(summary = "Список статусов заказов")
    public ResponseEntity<List<OrderStatusOptionDto>> getOrderStatuses() {
        return ResponseEntity.ok(adminOrderService.getOrderStatuses());
    }
}
