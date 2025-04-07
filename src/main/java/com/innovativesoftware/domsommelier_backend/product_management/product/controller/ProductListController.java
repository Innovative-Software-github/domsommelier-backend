package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductNewService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> searchProductsByName(@RequestParam String filter) {
        String jsonResponse = productService.searchProductsByFilter(filter);
        return ResponseEntity.ok(jsonResponse);
    }
}

