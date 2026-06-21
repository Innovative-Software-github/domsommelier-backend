package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.WineType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineTypeRepository extends JpaRepository<WineType, String> {
}
