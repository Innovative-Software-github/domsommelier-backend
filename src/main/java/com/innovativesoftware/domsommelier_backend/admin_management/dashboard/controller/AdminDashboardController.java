package com.innovativesoftware.domsommelier_backend.admin_management.dashboard.controller;

import com.innovativesoftware.domsommelier_backend.admin_management.dashboard.model.AdminDashboardStatsDto;
import com.innovativesoftware.domsommelier_backend.admin_management.dashboard.service.AdminDashboardService;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiresAdmin
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/stats")
@Tag(name = "Admin Dashboard", description = "Сводная статистика для главной страницы админки")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    @Operation(summary = "Сводные цифры дашборда (заказы/товары/мероприятия/винотеки)")
    public ResponseEntity<AdminDashboardStatsDto> getStats() {
        return ResponseEntity.ok(adminDashboardService.getStats());
    }
}
