package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterFieldProvider;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SpiritFilterFieldProvider implements ProductFilterFieldProvider {

    private final SpiritRepository spiritRepo;

    private static final Map<String, String> FIELD_RU_NAMES = Map.of(
            "subcategory", "Категория",
            "strength", "Крепкость",
            "producer", "Производитель",
            "country_name", "География",
            "volume", "Объем",
            "feature", "Особенности"
    );

    @Override
    public ProductCategoryEnum getSupportedCategory() {
        return ProductCategoryEnum.spirit;
    }

    @Override
    public Map<String, String> getFieldRuNames() {
        return FIELD_RU_NAMES;
    }

    @Override
    public Set<String> getLabels(String field) {
        return switch (field) {
            case "subcategory" -> spiritRepo.getDistinctSubcategory();
            case "strength" -> spiritRepo.getDistinctStrength();
            case "producer" -> spiritRepo.getDistinctProducer();
            case "volume" -> spiritRepo.getDistinctVolume();
            case "country_name" -> spiritRepo.findDistinctCountryNames();
            case "feature" -> spiritRepo.getDistinctFeature();
            default -> Set.of();
        };
    }
}
