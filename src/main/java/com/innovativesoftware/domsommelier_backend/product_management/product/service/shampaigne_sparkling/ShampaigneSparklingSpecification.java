package com.innovativesoftware.domsommelier_backend.product_management.product.service.shampaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.BaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ShampaigneSparklingSpecification extends BaseSpecification<SparklingWine> {
    @Override
    protected void addSpecificPredicates(Map<String, Object> params, Root<SparklingWine> root, CriteriaBuilder cb, List<Predicate> predicates) {
        params.forEach((key, value) -> {
            if (isEmpty(value)) return;
            switch (key) {
                case "subcategory" -> predicates.add(root.get("subcategory").get("name").in((List<?>) value));
                case "content" -> predicates.add(root.get("sugar_content").get("name").in((List<?>) value));
                case "volume" -> predicates.add(root.get("volume").get("name").in((List<?>) value));
                case "features" -> predicates.add(root.join("features").in((List<?>) value));
                case "color" -> predicates.add(root.join("color").get("name").in((List<?>) value));
            }
        });
    }
}
