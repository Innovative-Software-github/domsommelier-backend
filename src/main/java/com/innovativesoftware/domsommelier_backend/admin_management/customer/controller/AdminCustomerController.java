package com.innovativesoftware.domsommelier_backend.admin_management.customer.controller;

import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerDetailDto;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerFilterRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerListDto;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerOrderDto;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.UpdateCustomerDiscountRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.customer.service.AdminCustomerService;
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
@RequestMapping("/api/v1/admin/customers")
@Tag(name = "Admin Customers", description = "Клиенты: просмотр и личные скидки (admin)")
public class AdminCustomerController {

    private final AdminCustomerService adminCustomerService;

    @GetMapping
    @Operation(summary = "Список клиентов с фильтрами")
    public ResponseEntity<Page<AdminCustomerListDto>> getCustomers(
            @ModelAttribute AdminCustomerFilterRequest filter,
            @PageableDefault(sort = "email", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(adminCustomerService.getCustomers(filter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Карточка клиента")
    public ResponseEntity<AdminCustomerDetailDto> getCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(adminCustomerService.getCustomer(id));
    }

    @PatchMapping("/{id}/discount")
    @Operation(summary = "Назначить или снять личную скидку клиента")
    public ResponseEntity<AdminCustomerDetailDto> updateDiscount(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerDiscountRequest request
    ) {
        return ResponseEntity.ok(adminCustomerService.updateDiscount(id, request));
    }

    @GetMapping("/{id}/orders")
    @Operation(summary = "Заказы клиента")
    public ResponseEntity<Page<AdminCustomerOrderDto>> getCustomerOrders(
            @PathVariable UUID id,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(adminCustomerService.getCustomerOrders(id, pageable));
    }
}
