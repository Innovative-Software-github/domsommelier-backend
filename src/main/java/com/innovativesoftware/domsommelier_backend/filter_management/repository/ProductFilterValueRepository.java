package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.ProductFilterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductFilterValueRepository extends JpaRepository<ProductFilterValue, UUID> {
    List<ProductFilterValue> findAllByProductId(UUID productId);

    // Ищется список всех связок с продуктами, где value фильтрации LIKE value фильтрации
    @Query(value = """
        SELECT DISTINCT *
        FROM product_filter_value pfv LEFT JOIN filter_option fo ON
        LOWER(fo.value) LIKE LOWER(%:filterOptionValue%) AND fo.filter_id = :filterId
        WHERE pfv.filter_id = :filterId
    """, nativeQuery = true)
    List<ProductFilterValue> findAllByFilterIdAndOptionValue(UUID filterId, String filterOptionValue);

    @Query(value = """
        SELECT DISTINCT *
        FROM product_filter_value pfv
        WHERE pfv.filter_option_id IN (:filterOptions)
    """, nativeQuery = true)
    Set<ProductFilterValue> findAllByFilterOptionIdIn(List<UUID> filterOptions);

    // Логика такая: есть фильтр и промежуток, где надо посмотреть все продукты, которые подходят под этот промежуток
    @Query(value = """
        SELECT DISTINCT *
        FROM product_filter_value pfv
        WHERE pfv.filter_id = :filterId
        AND CAST(pfv.option AS FLOAT) BETWEEN :left AND :right
    """, nativeQuery = true)
    Set<ProductFilterValue> findAllByFilterIdAndValueBetween(UUID filterId, float left, float right);

    @Query(value = """
        SELECT EXISTS(SELECT 1 FROM product_filter_value pfv
        WHERE pfv.product_id = :productId AND pfv.filter_id = :filterId AND pfv.filter_option_id = :option)
    """, nativeQuery = true)
    boolean existsByProductIdAndFilterIdAndOption(UUID productId, UUID filterId, UUID option);
}
