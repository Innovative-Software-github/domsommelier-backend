package com.innovativesoftware.domsommelier_backend.product_management.product.service.low_alcohol;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcohol;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.BaseSpecification;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.RussianLabelTranslator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LowAlcoholSpecification extends BaseSpecification<LowAlcohol> {
    @Override
    protected void addSpecificPredicates(Map<String, Object> params, Root<LowAlcohol> root, CriteriaBuilder cb, List<Predicate> predicates) {
        params.forEach((key, value) -> {
            if (isEmpty(value)) return;
            switch (key) {
                // В БД коды (cider…), а фронт шлёт русскую подпись — см. RussianLabelTranslator.
                case "subcategory" -> predicates.add(root.get("category").get("name").in(untranslate((List<?>) value, RussianLabelTranslator::untranslateLowAlcoholCategory)));
                // Крепость — range-фильтр ([от, до]), а тут было IN: диапазон 3–8 находил только 3 и 8.
                case "strength" -> addRangePredicates(value, root.<Number>get("strength"), cb, predicates);
                case "volume" -> predicates.add(root.get("volume").in((List<?>) value));
                case "features" -> predicates.add(root.join("features").in((List<?>) value));
            }
        });
    }

    @Override
    protected Expression<?> specificFacetValue(String field, Root<LowAlcohol> root) {
        return switch (field) {
            case "subcategory" -> root.get("category").get("name");
            case "volume" -> root.get("volume");
            case "features" -> root.join("features");
            default -> null;
        };
    }

    @Override
    public String facetLabel(String field, String rawValue) {
        return "subcategory".equals(field) ? RussianLabelTranslator.translateLowAlcoholCategory(rawValue) : rawValue;
    }
}
