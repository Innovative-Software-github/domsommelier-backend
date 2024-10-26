package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.entity.ProductCountry;
import com.innovativesoftware.domsommelier_backend.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductCountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    @Autowired
    private ProductCountryRepository productCountryRepository;

    public List<ProductCountryProjection> getCountriesWithWines() {
        return productCountryRepository.getCountriesWithProductCategory(ProductCategories.WINE);
    }
}
