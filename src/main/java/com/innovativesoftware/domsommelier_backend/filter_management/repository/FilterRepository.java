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
            f.name = :name OR
            f.field = :field
            """)
    List<Filter> findByNameOrFieldIgnoreCase(String name, String field);

    @Query("SELECT f FROM Filter f WHERE f.productCategory = :productCategories")
    List<Filter> findByProductCategories(ProductCategories productCategories);

    @Query("SELECT EXISTS(SELECT 1 FROM Filter f WHERE f.name = :name AND f.productCategory = :productCategory)")
    boolean existsByNameAndProductCategories(String name, ProductCategories productCategory);

    @Query("SELECT EXISTS(SELECT 1 FROM Filter f WHERE f.field = :field AND f.productCategory = :productCategory)")
    boolean existsByFieldAndProductCategories(String field, ProductCategories productCategory);
}
