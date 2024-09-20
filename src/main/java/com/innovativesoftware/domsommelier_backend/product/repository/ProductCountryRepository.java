package com.innovativesoftware.domsommelier_backend.product.repository;

import com.innovativesoftware.domsommelier_backend.entity.ProductCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCountryRepository extends JpaRepository<ProductCountry, String> {
}