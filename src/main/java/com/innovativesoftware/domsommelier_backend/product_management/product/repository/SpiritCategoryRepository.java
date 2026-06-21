package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.SpiritCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpiritCategoryRepository extends JpaRepository<SpiritCategory, String> {
}
