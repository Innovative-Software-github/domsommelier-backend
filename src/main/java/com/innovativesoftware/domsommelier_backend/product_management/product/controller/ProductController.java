package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.WineWithPhotoDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductNewService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductNewService productNewService;

    @Autowired
    private ProductService productService;

    @Autowired
    private WineService wineService;

    @GetMapping("/new")
    public List<ProductNewDTO> getNewProducts() {
        return productNewService.getNewProducts();
    }

    @GetMapping("/wine")
    public List<WineWithPhotoDTO> getWines() {
        return wineService.findAllWines();
    }

    @GetMapping(value = "/wine", params = { "country" })
    public List<WineWithPhotoDTO> getWinesByCountry(@RequestParam String country) {
        return wineService.findWinesByCountry(country);
    }

    @GetMapping("/wine/countries")
    public List<ProductCountryProjection> getCountriesWithWines() {
        return productService.getCountriesWithWines();
    }
}

