package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("DEFAULT")
@RequiredArgsConstructor
public class CriteriaFilterStrategy implements ProductFilterStrategy {
    private final CreateQuery createQuery;
    private final EntityManager entityManager;
    private final ProductRepository productRepository;

    @Override
    public List<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        ProductCategoryEnum category = ProductCategoryEnum.valueOf((String) params.get("category"));

        CriteriaQuery<UUID> criteriaQuery = createQuery.createQuery(category, params, pageable);
        List<UUID> ids = entityManager.createQuery(criteriaQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return ids.stream()
                .map(id -> {
                    var product = productRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    return ProductMapper.toCardDto(product);
                })
                .toList();
    }
}
