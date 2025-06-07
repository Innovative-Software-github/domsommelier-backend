package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.service.FilterService;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "filter-controller", description = "Контроллер для работы с фильтрами")
@RequestMapping("/api/v1/filters")
@RequiredArgsConstructor
public class FilterController {
    private final FilterService filterService;

    @GetMapping
    @Operation(summary = "Получить все фильтры")
    public ResponseEntity<Map<String, Map<String, FilterDto>>> getAll() {
        return ResponseEntity.ok(filterService.getAllFilters());
    }

    @Hidden
    @PostMapping
    @Operation(summary = "Создать новый фильтр")
    public ResponseEntity<UUID> create(
            @RequestBody HashMap<String, Object> filterDTO
    ) {
        return ResponseEntity.ok(filterService.create(filterDTO));
    }

    @Hidden
    @GetMapping("/name/{name}")
    @Operation(summary = "Получить фильтр по имени и категории продукта")
    public ResponseEntity<FilterDto> getByName(
            @PathVariable String name,
            @RequestParam("productCategory") ProductCategoryEnum productCategoryEnum
    ) {
        return ResponseEntity.ok(filterService.getByName(name, productCategoryEnum));
    }

    @Hidden
    @GetMapping("/{id}")
    @Operation(summary = "Получить фильтр по идентификатору")
    public ResponseEntity<FilterDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(filterService.getById(id));
    }

    @Hidden
    @PutMapping("/{id}")
    @Operation(summary = "Обновить фильтр по идентификатору")
    public ResponseEntity<FilterDto> update(@PathVariable UUID id, @RequestBody Map<String, Object> filterDTO) {
        return ResponseEntity.ok(filterService.update(id, filterDTO));
    }

    @Hidden
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить фильтр по идентификатору")
    public ResponseEntity<UUID> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(filterService.delete(id));
    }

    @Hidden
    @GetMapping("/category/{categoryName}")
    @Operation(summary = "Получить все фильтры по категории")
    public ResponseEntity<List<FilterDto>> getByCategory(@PathVariable ProductCategoryEnum categoryName) {
        return ResponseEntity.ok(filterService.getByCategory(categoryName));
    }

    @Hidden
    @GetMapping("/category")
    @Operation(summary = "Получение всех фильтров по категории продукта")
    public ResponseEntity<Map<String, Map<String, FilterDto>>> getFiltersByProductCategory(
            @RequestParam("productCategory") ProductCategoryEnum productCategoryEnum
    ) {
        return ResponseEntity.ok(filterService.getFiltersByProductCategory(productCategoryEnum));
    }

    @Hidden
    @GetMapping("/filterTypes")
    @Operation(summary = "Получение всех типов фильтров")
    public ResponseEntity<List<String>> getFilterTypes() {
        return ResponseEntity.ok(filterService.getFilterTypes());
    }
}
