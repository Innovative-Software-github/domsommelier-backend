package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
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
public class WineSpecification extends BaseSpecification<Wine> {
    @Override
    protected void addSpecificPredicates(Map<String, Object> params, Root<Wine> root, CriteriaBuilder cb, List<Predicate> predicates) {
        params.forEach((key, value) -> {
            if (isEmpty(value)) return;
            switch (key) {
                case "type" -> predicates.add(root.get("type").get("name").in((List<?>) value));
                case "color" -> predicates.add(root.get("color").get("name").in((List<?>) value));
                case "volume" -> predicates.add(root.get("volume").in((List<?>) value));
                // Фронтенд шлёт выбранный label (см. MultiSelectFilter.tsx — матчит
                // по label, не по value), а в БД до сих пор лежит исходный
                // английский код (grape/feature не мигрировали на русский текст,
                // в отличие от color/type) — переводим обратно перед запросом.
                case "features" -> predicates.add(root.join("features").in(untranslate((List<?>) value, RussianLabelTranslator::untranslateFeature)));
                case "grape" -> predicates.add(cb.or(untranslate((List<?>) value, RussianLabelTranslator::untranslateGrape).stream()
                    .map(code -> cb.isMember(code, root.<java.util.Collection<String>>get("grapes"))).toArray(Predicate[]::new)));
                case "year" -> addRangePredicates(value, root.<Number>get("productionYear"), cb, predicates);
                case "in_stock" -> predicates.add(cb.equal(root.get("product").get("inStock"), value));
            }
        });
    }

    @Override
    protected Expression<?> specificFacetValue(String field, Root<Wine> root) {
        return switch (field) {
            case "type" -> root.get("type").get("name");
            case "color" -> root.get("color").get("name");
            case "volume" -> root.get("volume");
            case "features" -> root.join("features");
            case "grape" -> root.join("grapes");
            default -> null;
        };
    }

    @Override
    public String facetLabel(String field, String rawValue) {
        return switch (field) {
            case "features" -> RussianLabelTranslator.translateFeature(rawValue);
            case "grape" -> RussianLabelTranslator.translateGrape(rawValue);
            default -> rawValue;
        };
    }
}
