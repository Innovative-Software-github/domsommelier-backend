package com.innovativesoftware.domsommelier_backend.product_management.product.service.accessories;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories.Accessories;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.BaseSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AccessoriesSpecification extends BaseSpecification<Accessories> {
    @Override
    protected void addSpecificPredicates(Map<String, Object> params, Root<Accessories> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // Только общие (price, countries, producer, description) – тут ничего специфичного
        // Поэтому реализовывать ничего не нужно, но метод должен быть
    }
}
