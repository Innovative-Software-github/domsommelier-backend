package com.innovativesoftware.domsommelier_backend.product_management.store.controller;

import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreFilterDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreRequestDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreResponseDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.service.WineStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.ok(wineStoreService.getAllWineStores(page, size, filter));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить винотеку по ID")
    public ResponseEntity<WineStoreResponseDto> getWineStoreById(@PathVariable Long id) {
        return ResponseEntity.ok(wineStoreService.getWineStoreById(id));
    }

    @PostMapping
    @Operation(summary = "Создать винотеку")
    public ResponseEntity<WineStoreResponseDto> createWineStore(@RequestBody @Valid WineStoreRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wineStoreService.createWineStore(requestDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные винотеки")
    public ResponseEntity<WineStoreResponseDto> updateWineStore(
            @PathVariable Long id,
            @RequestBody @Valid WineStoreRequestDto requestDto
    ) {
        return ResponseEntity.ok(wineStoreService.updateWineStore(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить винотеку")
    public ResponseEntity<Void> deleteWineStore(@PathVariable Long id) {
        wineStoreService.deleteWineStore(id);
        return ResponseEntity.noContent().build();
    }
}
