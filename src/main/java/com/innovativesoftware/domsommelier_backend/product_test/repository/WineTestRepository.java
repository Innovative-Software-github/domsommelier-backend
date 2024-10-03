package com.innovativesoftware.domsommelier_backend.product_test.repository;

import com.innovativesoftware.domsommelier_backend.product_test.model.ProductTest;
import com.innovativesoftware.domsommelier_backend.product_test.model.WineTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineTestRepository extends JpaRepository<WineTest, ProductTest> {
}