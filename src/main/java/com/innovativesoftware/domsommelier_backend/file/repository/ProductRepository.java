package com.innovativesoftware.domsommelier_backend.file.repository;

import com.innovativesoftware.domsommelier_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}