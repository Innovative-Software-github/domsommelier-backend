package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.SnackCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnackCategoryRepository extends JpaRepository<SnackCategory, String> {
}
