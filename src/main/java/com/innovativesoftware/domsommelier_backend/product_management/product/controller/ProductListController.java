package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductNewService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductListController {

    @Autowired
    private ProductNewService productNewService;

    @Autowired
    private ProductService productService;

    @GetMapping("/new")
    public List<ProductNewDTO> getNewProducts() {
        return productNewService.getNewProducts();
    }

    @GetMapping("/categories")
    public List<ProductCategoryProjection> getProductCategories() {
        return productService.getProductCategories();
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@RequestParam("id") String id) {
        return ResponseEntity.ok(productService.getProductDetails(UUID.fromString(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductCardDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/all/category")
    public ResponseEntity<List<ProductCardDto>> getAllProductCategories(
            @RequestParam("productCategory") String productCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProductsByCategory(productCategory, pageable));
    }

    @GetMapping("/all/country")
    public ResponseEntity<List<ProductCardDto>> getAllProductCountries(
            @RequestParam("country") String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProductsByCountry(country, pageable));
    }
}
