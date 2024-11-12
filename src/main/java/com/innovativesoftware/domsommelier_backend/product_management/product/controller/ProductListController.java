package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductNewService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/products")
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
}

