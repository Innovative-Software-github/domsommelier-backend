package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductReferenceDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.AdminProductService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@RequiresAdmin
@Tag(name = "Admin Products", description = "Управление товарами (CRUD)")
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Список товаров категории (поиск, пагинация)")
    public Page<ProductCardDto> list(
            @RequestParam ProductCategoryEnum category,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return adminProductService.list(category, search, pageable);
    }

    @PostMapping
    @Operation(summary = "Создать товар")
    public ResponseEntity<ProductDTO> create(@RequestBody @Valid ProductWriteRequest request) {
        UUID id = adminProductService.create(request);
        // Чтение отдельной транзакцией: свежесозданный entity имеет неинициализированные коллекции.
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.getProductDetails(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар")
    public ProductDTO update(@PathVariable UUID id, @RequestBody @Valid ProductWriteRequest request) {
        adminProductService.update(id, request);
        return productService.getProductDetails(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар (409, если на товар есть заказы)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminProductService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{category}/reference")
    @Operation(summary = "Справочники для формы товара указанной категории")
    public ProductReferenceDto getReference(@PathVariable ProductCategoryEnum category) {
        return adminProductService.getReference(category);
    }
}
