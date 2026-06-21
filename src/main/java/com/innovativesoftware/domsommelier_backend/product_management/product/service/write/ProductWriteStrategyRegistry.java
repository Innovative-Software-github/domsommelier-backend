package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class ProductWriteStrategyRegistry {

    private final List<ProductWriteStrategy> strategies;

    public ProductWriteStrategyRegistry(List<ProductWriteStrategy> strategies) {
        this.strategies = strategies;
    }

    public ProductWriteStrategy get(ProductCategoryEnum category) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(category))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Управление товарами категории '" + category + "' пока не реализовано"));
    }
}
