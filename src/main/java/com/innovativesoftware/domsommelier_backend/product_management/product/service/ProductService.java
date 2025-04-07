package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.google.gson.Gson;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final Gson gson;
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private ProductCountryRepository productCountryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public List<ProductCountryProjection> getCountriesWithWines() {
        return productCountryRepository.getCountriesWithProductCategory(ProductCategories.WINE);
    }

    public List<ProductCategoryProjection> getProductCategories() {
        return productCategoryRepository.findAllCategories();
    }

    public String searchProductsByFilter(String filter) {
        List<UUID> products = productRepository.findByFilterContainingIgnoreCase(filter);
        return gson.toJson(products);
    }
}
