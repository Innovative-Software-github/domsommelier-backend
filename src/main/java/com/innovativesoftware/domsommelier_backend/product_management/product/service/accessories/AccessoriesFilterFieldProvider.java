package com.innovativesoftware.domsommelier_backend.product_management.product.service.accessories;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterFieldProvider;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.AccessoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccessoriesFilterFieldProvider implements ProductFilterFieldProvider {

    private final AccessoriesRepository accessoriesRepo;

    private static final Map<String, String> FIELD_RU_NAMES = Map.of(
            "country_name", "География",
            "producer", "Производитель",
            "feature", "Особенности"
    );

    @Override
    public ProductCategoryEnum getSupportedCategory() {
        return ProductCategoryEnum.accessories;
    }

    @Override
    public Map<String, String> getFieldRuNames() {
        return FIELD_RU_NAMES;
    }

    @Override
    public Set<String> getLabels(String field) {
        return switch (field) {
            case "country_name" -> accessoriesRepo.findDistinctCountries();
            case "producer" -> accessoriesRepo.findDistinctProducers();
            case "feature" -> accessoriesRepo.findDistinctFeatures();
            default -> Set.of();
        };
    }
}
