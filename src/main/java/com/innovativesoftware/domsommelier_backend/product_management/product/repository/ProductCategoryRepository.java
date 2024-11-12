package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    @Query("""
        select productCategory.name as name, productCategory.label as label 
        from ProductCategory productCategory
    """)
    List<ProductCategoryProjection> findAllCategories();
}