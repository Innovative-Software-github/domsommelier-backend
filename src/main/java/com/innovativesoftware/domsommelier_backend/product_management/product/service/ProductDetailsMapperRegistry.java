package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductDetailsMapperRegistry {
    private final List<ProductDetailsMapper<?>> mappers;

    @Autowired
    public ProductDetailsMapperRegistry(List<ProductDetailsMapper<?>> mappers) {
        this.mappers = mappers;
    }

    public Optional<ProductDetailsMapper<?>> findMapper(ProductCategoryEnum category) {
        return mappers.stream()
                .filter(mapper -> mapper.supports(category))
                .findFirst();
    }
}
