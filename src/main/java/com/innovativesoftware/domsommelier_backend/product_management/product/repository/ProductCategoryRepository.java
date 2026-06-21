package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    @Query("""
        select productCategoryEnum.name as name, productCategoryEnum.label as label
        from ProductCategory productCategoryEnum
    """)
    List<ProductCategoryProjection> findAllCategories();

    Optional<ProductCategory> findByName(ProductCategoryEnum name);
}