package com.innovativesoftware.domsommelier_backend.product.controller;

import com.innovativesoftware.domsommelier_backend.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product.model.ProductPhotoDTO;
import com.innovativesoftware.domsommelier_backend.product.model.WineDTO;
import com.innovativesoftware.domsommelier_backend.product.model.WineWithPhotoDTO;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product.service.ProductNewService;
import com.innovativesoftware.domsommelier_backend.product.service.WineService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductNewService productNewService;

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
}

