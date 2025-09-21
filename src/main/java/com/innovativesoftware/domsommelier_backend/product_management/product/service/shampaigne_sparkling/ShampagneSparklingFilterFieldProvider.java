package com.innovativesoftware.domsommelier_backend.product_management.product.service.shampaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterFieldProvider;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShampagneSparklingFilterFieldProvider implements ProductFilterFieldProvider {

    private final SparklingWineRepository sparklingWineRepo;

    private static final Map<String, String> FIELD_RU_NAMES = Map.of(
            "countries", "География",
            "subcategory", "Субкатегория",
            "sugarContent", "Содержание сахара",
            "producer", "Производитель",
            "color", "Цвет",
            "volume", "Объем",
            "features", "Особенности"
    );

    @Override
    public ProductCategoryEnum getSupportedCategory() {
        return ProductCategoryEnum.champagne_and_sparkling;
    }

    @Override
    public Map<String, String> getFieldRuNames() {
        return FIELD_RU_NAMES;
    }

    @Override
    public Set<String> getLabels(String field) {
        return switch (field) {
            case "subcategory" -> sparklingWineRepo.findDistinctSubcategories();
            case "countries" -> sparklingWineRepo.findDistinctCountryNames();
            case "sugarContent" -> sparklingWineRepo.findDistinctSugarContents();
            case "producer" -> sparklingWineRepo.findDistinctProducers();
            case "color" -> sparklingWineRepo.findDistinctColors();
            case "volume" -> sparklingWineRepo.findDistinctVolumes();
            case "features" -> sparklingWineRepo.findDistinctFeatures();
            default -> Set.of();
        };
    }
}
