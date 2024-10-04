package com.innovativesoftware.domsommelier_backend.product.repository;

import com.innovativesoftware.domsommelier_backend.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}