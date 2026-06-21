package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcoholCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LowAlcoholCategoryRepository extends JpaRepository<LowAlcoholCategory, String> {
}
