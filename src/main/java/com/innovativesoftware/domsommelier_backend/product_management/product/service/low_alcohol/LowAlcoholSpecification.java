package com.innovativesoftware.domsommelier_backend.product_management.product.service.low_alcohol;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcohol;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.BaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
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
                case "subcategory" -> predicates.add(root.get("category").get("name").in((List<?>) value));
                case "strength" -> predicates.add(root.get("strength").in((List<?>) value));
                case "volume" -> predicates.add(root.get("volume").in((List<?>) value));
                case "features" -> predicates.add(root.join("features").in((List<?>) value));
            }
        });
    }
}
