package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductFilterStrategyFactory {
    private final Map<String, ProductFilterStrategy> strategies;

    public ProductFilterStrategy getStrategy(ProductCategoryEnum category) {
        // По умолчанию используем "DEFAULT", если нет специфичной стратегии
        return strategies.getOrDefault(category.name(), strategies.get("DEFAULT"));
    }
}