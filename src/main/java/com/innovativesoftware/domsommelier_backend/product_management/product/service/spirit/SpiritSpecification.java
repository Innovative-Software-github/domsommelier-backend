package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
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
public class SpiritSpecification extends BaseSpecification<Spirit> {
    @Override
    protected void addSpecificPredicates(Map<String, Object> params, Root<Spirit> root, CriteriaBuilder cb, List<Predicate> predicates) {
        params.forEach((key, value) -> {
            if (isEmpty(value)) return;
            switch (key) {
                case "subcategory" -> predicates.add(root.get("category").get("name").in((List<?>) value));
                case "strength" -> addRangePredicates(value, root.<Number>get("strength"), cb, predicates);
                case "volume" -> predicates.add(root.get("volume").in((List<?>) value));
                // См. WineSpecification — фронтенд шлёт label, а в БД лежит
                // исходный английский код, переводим обратно перед запросом.
                case "features" -> predicates.add(root.join("features").in(untranslate((List<?>) value, RussianLabelTranslator::untranslateFeature)));
            }
        });
    }

    @Override
    protected Expression<?> specificFacetValue(String field, Root<Spirit> root) {
        return switch (field) {
            case "subcategory" -> root.get("category").get("name");
            case "volume" -> root.get("volume");
            case "features" -> root.join("features");
            default -> null;
        };
    }

    @Override
    public String facetLabel(String field, String rawValue) {
        return "features".equals(field) ? RussianLabelTranslator.translateFeature(rawValue) : rawValue;
    }
}
