package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFilterStrategyFactory {

    private final List<ProductFilterStrategy> strategyList;
    private Map<String, ProductFilterStrategy> strategies;

    @PostConstruct
    void init() {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strat -> strat.getCategoryEnum().name(),
                        Function.identity()
                ));
    }

    public ProductFilterStrategy getStrategy(ProductCategoryEnum category) {
        return strategies.getOrDefault(category.name(), strategies.get("DEFAULT"));
    }
}
