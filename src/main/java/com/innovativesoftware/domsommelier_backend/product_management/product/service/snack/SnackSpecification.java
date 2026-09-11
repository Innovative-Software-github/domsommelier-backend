package com.innovativesoftware.domsommelier_backend.product_management.product.service.snack;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.Snack;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.BaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SnackSpecification extends BaseSpecification<Snack> {
    @Override
    protected void addSpecificPredicates(Map<String, Object> params, Root<Snack> root, CriteriaBuilder cb, List<Predicate> predicates) {
        params.forEach((key, value) -> {
            if (isEmpty(value)) return;
            switch (key) {
                // В filters.sql поля называются subcategory и smart_selection, а здесь ждали
                // snack_category и pairings — фильтры закусок молча ничего не делали.
                case "subcategory", "snack_category" -> predicates.add(root.get("subcategory").get("name").in((List<?>) value));
                case "smart_selection", "pairings" -> predicates.add(root.join("pairings").in((List<?>) value));
            }
        });
    }

    @Override
    protected Expression<?> specificFacetValue(String field, Root<Snack> root) {
        return switch (field) {
            case "subcategory" -> root.get("subcategory").get("name");
            case "smart_selection" -> root.join("pairings");
            default -> null;
        };
    }
}
