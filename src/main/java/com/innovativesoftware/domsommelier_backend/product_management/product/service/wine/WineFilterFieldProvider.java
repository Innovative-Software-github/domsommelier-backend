package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterFieldProvider;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WineFilterFieldProvider implements ProductFilterFieldProvider {

    private final WineRepository wineRepo;

    private static final Map<String, String> FIELD_RU_NAMES = Map.of(
            "color", "Цвет",
            "type", "Содержание сахара",
            "grape", "Сорт винограда",
            "feature", "Особенности",
            "country_name", "География",
            "producer", "Производитель",
            "volume", "Объем"
    );

    @Override
    public ProductCategoryEnum getSupportedCategory() {
        return ProductCategoryEnum.wine;
    }

    @Override
    public Map<String, String> getFieldRuNames() {
        return FIELD_RU_NAMES;
    }

    @Override
    public Set<String> getLabels(String field) {
        return switch (field) {
            case "color" -> wineRepo.findDistinctColors();
            case "type" -> wineRepo.findDistinctTypes();
            case "grape" -> wineRepo.findDistinctGrapes();
            case "feature" -> wineRepo.findDistinctFeatures();
            case "country_name" -> wineRepo.findDistinctCountryNames();
            case "producer" -> wineRepo.findDistinctProducers();
            case "volume" -> wineRepo.findDistinctVolumes();
            default -> Set.of();
        };
    }
}
