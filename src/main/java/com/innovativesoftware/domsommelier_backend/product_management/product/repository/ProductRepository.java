package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
}