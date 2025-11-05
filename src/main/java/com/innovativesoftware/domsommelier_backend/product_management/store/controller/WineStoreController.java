package com.innovativesoftware.domsommelier_backend.product_management.store.controller;

import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreFilterDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreResponseDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.service.WineStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "wine-store-controller", description = "Работа с винными магазинами")
@RequestMapping("/api/v1/wine-stores")
@RequiredArgsConstructor
@Validated
public class WineStoreController {

    private final WineStoreService wineStoreService;

    @GetMapping
    @Operation(summary = "Получить список винных магазинов")
    public ResponseEntity<Page<WineStoreResponseDto>> getAllWineStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @ModelAttribute WineStoreFilterDto filter
    ) {
        Page<WineStoreResponseDto> wineStores = wineStoreService.getAllWineStores(page, size, filter);
        return ResponseEntity.ok(wineStores);
    }
}
