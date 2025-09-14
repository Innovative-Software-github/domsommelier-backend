package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.BaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
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
                case "features" -> predicates.add(root.join("features").in((List<?>) value));
                case "grape" -> predicates.add(root.join("grapes").in((List<?>) value));
                case "year" -> {
                    if (value instanceof List<?> yearRange && yearRange.size() == 2) {
                        predicates.add(cb.ge(root.get("productionYear"), toNumber(yearRange.get(0))));
                        predicates.add(cb.le(root.get("productionYear"), toNumber(yearRange.get(1))));
                    }
                }
                case "in_stock" -> predicates.add(cb.equal(root.get("product").get("inStock"), value));
            }
        });
    }
}
