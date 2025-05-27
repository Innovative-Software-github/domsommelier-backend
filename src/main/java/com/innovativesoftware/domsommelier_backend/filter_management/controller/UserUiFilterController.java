package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.service.FilterService;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "UserUiFilterController", description = "Контроллер управления фильтрами для пользователя (поиск)")
@RequestMapping("/api/v1/user/filters")
@RequiredArgsConstructor
public class UserUiFilterController {
    private final FilterService filterService;
    private final ProductService productService;

    @GetMapping("/productCategory")
    @Operation(summary = "Получение всех фильтров по категории продукта")
    public ResponseEntity<List<UUID>> getFiltersByProductCategory(
            @RequestParam("productCategoryId") ProductCategoryEnum productCategoryEnum
    ) {
        return ResponseEntity.ok(filterService.getFiltersByProductCategory(productCategoryEnum));
    }

    @GetMapping("/filterTypes")
    @Operation(summary = "Получение всех типов фильтров")
    public ResponseEntity<List<String>> getFilterTypes() {
        return ResponseEntity.ok(filterService.getFilterTypes());
    }

    @PostMapping("/filter")
    @Operation(summary = "Поиск продуктов по фильтру")
    public ResponseEntity<List<ProductCardDto>> getByFilterIdAndFilterOptionId(
            @RequestParam("category") ProductCategoryEnum category,
            @RequestBody Map<String, Object> params
    ) {
        return ResponseEntity.ok(productService.getAllByFilters(category, params));
    }
}
