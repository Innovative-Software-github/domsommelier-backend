package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProductCardDtoMapperRegistry {

    private final Map<String, ProductCardDtoMapper> mappers;

    public ProductCardDtoMapperRegistry(List<ProductCardDtoMapper> mappers) {
        this.mappers = mappers.stream()
                .collect(Collectors.toMap(
                        ProductCardDtoMapper::getSupportedCategory, // например, возвращает "Вино"
                        Function.identity()
                ));
    }

    public ProductCardDtoMapper getMapper(String categoryName) {
        return mappers.getOrDefault(categoryName, mappers.get("default"));
    }
}