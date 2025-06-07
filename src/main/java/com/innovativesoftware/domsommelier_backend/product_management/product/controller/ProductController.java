package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductNewService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "product-controller", description = "Работа с продуктами")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductNewService productNewService;

    @Autowired
    private ProductService productService;

    @Hidden
    @GetMapping("/new")
    public List<ProductNewDTO> getNewProducts() {
        return productNewService.getNewProducts();
    }

    @Hidden
    @Operation(summary = "Получение категорий продуктов")
    @GetMapping("/categories")
    public List<ProductCategoryProjection> getProductCategories() {
        return productService.getProductCategories();
    }

    @Hidden
    @Operation(summary = "Получение всех продуктов по названию")
    @GetMapping("/search")
    public ResponseEntity<String> searchProductsByName(
            @RequestHeader String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        String jsonResponse = productService.searchProductsByName(name, pageable);
        return ResponseEntity.ok(jsonResponse);
    }

    @Hidden
    @Operation(summary = "Получение продукта по id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@RequestParam("id") String id) {
        return ResponseEntity.ok(productService.getProductDetails(UUID.fromString(id)));
    }

    @Hidden
    @Operation(summary = "Получение всех продуктов")
    @GetMapping("/all")
    public ResponseEntity<List<ProductCardDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @Hidden
    @Operation(summary = "Получение продуктов по категории")
    @GetMapping("/all/category")
    public ResponseEntity<List<ProductCardDto>> getAllProductCategories(
            @RequestParam("productCategory") ProductCategoryEnum productCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProductsByCategory(productCategory, pageable));
    }

    @Hidden
    @Operation(summary = "Получение продуктов по стране")
    @GetMapping("/all/country")
    public ResponseEntity<List<ProductCardDto>> getAllProductCountries(
            @RequestParam("country") String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProductsByCountry(country, pageable));
    }

    @PostMapping("/filter")
    @Operation(summary = "Поиск продуктов по фильтру")
    public ResponseEntity<List<ProductCardDto>> getByFilterIdAndFilterOptionId(
            @RequestParam("category") ProductCategoryEnum category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestBody Map<String, Object> params
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllByFilters(category, params, pageable));
    }

    @Hidden
    @GetMapping("/countries")
    @Operation(summary = "Получение стран продуктов")
    public ResponseEntity<List<String>> getAllCountries() {
        return ResponseEntity.ok(productService.getAllCountries());
    }
}
