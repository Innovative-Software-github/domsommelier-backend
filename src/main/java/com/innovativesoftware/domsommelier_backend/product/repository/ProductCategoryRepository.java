package com.innovativesoftware.domsommelier_backend.product.repository;

import com.innovativesoftware.domsommelier_backend.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
}