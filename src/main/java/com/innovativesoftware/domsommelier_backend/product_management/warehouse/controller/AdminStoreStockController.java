package com.innovativesoftware.domsommelier_backend.product_management.warehouse.controller;

import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.model.StockUpdateRequest;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.model.StoreStockItemDto;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.service.StoreStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/admin/wine-stores/{storeId}/stock")
@RequiredArgsConstructor
@RequiresAdmin
@Tag(name = "Admin Store Stock", description = "Управление остатками товаров по винотекам")
public class AdminStoreStockController {

    private final StoreStockService storeStockService;

    @GetMapping
    @Operation(summary = "Склад винотеки: ассортимент с остатками")
    public Page<StoreStockItemDto> getStoreStock(
            @PathVariable Long storeId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return storeStockService.getStoreStock(storeId, search, pageable);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Установить остаток товара в винотеке")
    public StoreStockItemDto setStock(
            @PathVariable Long storeId,
            @PathVariable UUID productId,
            @RequestBody @Valid StockUpdateRequest request
    ) {
        return storeStockService.setStock(storeId, productId, request.getQuantity());
    }
}
