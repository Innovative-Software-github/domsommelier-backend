package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.CountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public List<CountryProjection> getCountriesWithWines() {
        return countryRepository.getCountriesWithProductCategory(ProductCategories.WINE);
    }

    public List<ProductCategoryProjection> getProductCategories() {
        return productCategoryRepository.findAllCategories();
    }
}
