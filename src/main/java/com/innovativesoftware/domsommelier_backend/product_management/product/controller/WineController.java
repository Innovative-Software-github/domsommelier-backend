package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.WineWithPhotosDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductService;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/products/wines")
public class WineController {
    @Autowired
    private ProductService productService;

    @Autowired
    private WineService wineService;

    @GetMapping("")
    public List<WineWithPhotosDTO> getWines() {
        return wineService.findAllWines();
    }

    @GetMapping(value = "", params = { "country" })
    public List<WineWithPhotosDTO> getWinesByCountry(@RequestParam String country) {
        return wineService.findWinesByCountry(country);
    }

    @GetMapping("/countries")
    public List<ProductCountryProjection> getCountriesWithWines() {
        return productService.getCountriesWithWines();
    }
}

