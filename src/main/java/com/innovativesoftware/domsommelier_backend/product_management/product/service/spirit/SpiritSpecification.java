package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpiritSpecification {
    @SuppressWarnings("unchecked")
    public static Specification<Spirit> byFilter(Map<String, Object> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                if (isEmpty(value)) return;
                switch (key) {
                    case "price" -> {
                        if (value instanceof List<?> priceRange && priceRange.size() == 2) {
                            predicates.add(cb.ge(root.get("product").get("price"), toNumber(priceRange.get(0))));
                            predicates.add(cb.le(root.get("product").get("price"), toNumber(priceRange.get(1))));
                        }
                    }
                    case "category" -> predicates.add(root.get("category").get("name").in((List<?>) value));
                    case "strength" -> predicates.add(root.get("strength").get("name").in((List<?>) value));
                    case "volume" -> predicates.add(root.get("volume").get("name").in((List<?>) value));
                    case "features" -> predicates.add(root.join("features").in((List<?>) value));
                    case "countries" -> predicates.add(root.get("product").get("productCountry").get("name").in((List<?>) value));
                    case "producer" -> {
                        if (value instanceof List<?> l) {
                            predicates.add(root.get("producer").in(l));
                        } else {
                            predicates.add(cb.equal(root.get("producer"), value));
                        }
                    }
                    case "description" -> predicates.add(cb.like(cb.lower(root.get("product").get("description")),
                            "%" + value.toString().toLowerCase() + "%"));
                }
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof List<?> list) return list.isEmpty();
        if (value instanceof String str) return str.trim().isEmpty();
        return false;
    }

    private static Number toNumber(Object obj) {
        if (obj instanceof Number) return (Number) obj;
        if (obj instanceof String s) return Double.parseDouble(s);
        throw new IllegalArgumentException("Cannot convert to number: " + obj);
    }
}
