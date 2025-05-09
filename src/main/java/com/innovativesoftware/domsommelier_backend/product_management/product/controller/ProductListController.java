package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductNewService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<String> searchProductsByName(@RequestHeader String name) {
        String jsonResponse = productService.searchProductsByName(name);
        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@RequestParam("id") String id) {
        return ResponseEntity.ok(productService.getProductDetails(UUID.fromString(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UUID>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/all/category")
    public ResponseEntity<List<UUID>> getAllProductCategories(
            @RequestParam("productCategory") String productCategory
    ) {
        return ResponseEntity.ok(productService.getAllProductsByCategory(productCategory));
    }

    @GetMapping("/all/country")
    public ResponseEntity<List<UUID>> getAllProductCountries(
            @RequestParam("country") String country
    ) {
        return ResponseEntity.ok(productService.getAllProductsByCountry(country));
    }
}
