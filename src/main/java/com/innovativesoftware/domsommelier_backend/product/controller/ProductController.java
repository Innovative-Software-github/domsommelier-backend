package com.innovativesoftware.domsommelier_backend.product.controller;

import com.innovativesoftware.domsommelier_backend.product.DTO.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product.service.ProductNewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductNewService productNewService;

    @GetMapping("/new")
    public List<ProductNewDTO> getNewProducts() {
        return productNewService.getNewProducts();
    }
}

