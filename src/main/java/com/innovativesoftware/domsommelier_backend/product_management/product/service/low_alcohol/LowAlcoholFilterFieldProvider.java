package com.innovativesoftware.domsommelier_backend.product_management.product.service.low_alcohol;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterFieldProvider;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.LowAlcoholRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LowAlcoholFilterFieldProvider implements ProductFilterFieldProvider {

    private final LowAlcoholRepository lowAlcoholRepo;

    private static final Map<String, String> FIELD_RU_NAMES = Map.of(
            "subcategory", "Субкатегория",
            "countries", "География",
            "producer", "Производитель",
            "volume", "Объем"
    );

    @Override
    public ProductCategoryEnum getSupportedCategory() {
        return ProductCategoryEnum.low_alcohol;
    }

    @Override
    public Map<String, String> getFieldRuNames() {
        return FIELD_RU_NAMES;
    }

    @Override
    public Set<String> getLabels(String field) {
        return switch (field) {
            case "subcategory" -> lowAlcoholRepo.findDistinctCategories();
            case "countries" -> lowAlcoholRepo.findDistinctCountryNames();
            case "producer" -> lowAlcoholRepo.findDistinctProducers();
            case "volume" -> lowAlcoholRepo.findDistinctVolumes();
            default -> Set.of();
        };
    }
}
