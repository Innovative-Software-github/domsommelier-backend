package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FilterRepository extends JpaRepository<Filter, UUID> {
    @Query("""
            SELECT f FROM Filter f WHERE
            LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%')) OR
            LOWER(f.field) LIKE LOWER(CONCAT('%', :field, '%'))
            """)
    List<Filter> findByNameOrFieldIgnoreCase(String name, String field);

    @Query("SELECT f FROM Filter f WHERE f.productCategory = :productCategories")
    Filter findByProductCategories(ProductCategories productCategories);
}
