package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.BaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
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
                case "category" -> predicates.add(root.get("subcategory").get("name").in((List<?>) value));
                case "strength" -> predicates.add(root.get("strength").get("name").in((List<?>) value));
                case "volume" -> predicates.add(root.get("volume").get("name").in((List<?>) value));
                case "features" -> predicates.add(root.join("features").in((List<?>) value));
            }
        });
    }
}
