package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p.id FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :name, '%'))")
    List<UUID> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p.id FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(concat('%', :filter, '%')) OR " +
            "LOWER(p.productCategory.name) LIKE LOWER(concat('%', :filter, '%')) OR " +
            "LOWER(p.productCountry.name) LIKE LOWER(concat('%', :filter, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(concat('%', :filter, '%')) OR " +
            "LOWER(p.article) LIKE LOWER(concat('%', :filter, '%'))" +
            "ORDER BY " +
            "CASE " +
            "  WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', :filter, '%')) THEN 1 " +
            "  WHEN LOWER(p.productCategory.name) LIKE LOWER(CONCAT('%', :filter, '%')) THEN 2 " +
            "  WHEN LOWER(p.productCountry.name) LIKE LOWER(CONCAT('%', :filter, '%')) THEN 3 " +
            "  WHEN LOWER(p.description) LIKE LOWER(CONCAT('%', :filter, '%')) THEN 4 " +
            "  WHEN LOWER(p.article) LIKE LOWER(CONCAT('%', :filter, '%')) THEN 5 " +
            "  ELSE 6 " +
            "END ASC")
    List<UUID> findByFilterContainingIgnoreCase(@Param("filter") String filter);
}