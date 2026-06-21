package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProductSearchSpecification {

    private ProductSearchSpecification() {
    }

    public static Specification<Product> byQueryAndCity(String query, String city) {
        return (Root<Product> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String pattern = "%" + query.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("article")), pattern)
            ));

            if (city != null && !city.isBlank()) {
                Join<?, ?> stock = root.join("stocks");
                Join<?, ?> store = stock.join("wineStore");

                predicates.add(cb.equal(store.get("city"), city));
                predicates.add(cb.gt(stock.get("quantity"), 0));

                if (criteriaQuery != null) {
                    criteriaQuery.distinct(true);
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
