package com.innovativesoftware.domsommelier_backend.product_test.repository;

import com.innovativesoftware.domsommelier_backend.product_test.model.ProductTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductTestRepository extends JpaRepository<ProductTest, UUID> {
}