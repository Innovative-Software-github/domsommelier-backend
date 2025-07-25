package com.innovativesoftware.domsommelier_backend.product_management.product.service.snack;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterFieldProvider;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SnackFilterFieldProvider implements ProductFilterFieldProvider {

    private final SnackRepository snackRepo;

    private static final Map<String, String> FIELD_RU_NAMES = Map.of(
            "category", "Категория",
            "producer", "Производитель",
            "smart_selection", "Подбор к напиткам",
            "country_name", "География"
    );

    @Override
    public ProductCategoryEnum getSupportedCategory() {
        return ProductCategoryEnum.snack;
    }

    @Override
    public Map<String, String> getFieldRuNames() {
        return FIELD_RU_NAMES;
    }

    @Override
    public Set<String> getLabels(String field) {
        return switch (field) {
            case "category" -> snackRepo.findDistinctCategories();
            case "producer" -> snackRepo.findDistinctProducers();
            case "smart_selection" -> snackRepo.findDistinctPairings();
            case "country_name" -> snackRepo.findDistinctCountryNames();
            default -> Set.of();
        };
    }
}
