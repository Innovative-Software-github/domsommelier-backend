package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p.id FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :name, '%'))")
    List<UUID> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p.id FROM Product p WHERE p.productCategory.name = :category")
    List<UUID> findByProductCategory(@Param("category") ProductCategoryEnum category);

    @Query("SELECT p.id FROM Product p WHERE p.productCountry.name = :country")
    List<UUID> findByProductCountry(@Param("country") String country);

    @Query("SELECT p.id FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :name, '%')) AND p.productCategory.name = :category")
    List<UUID> findByNameContainingIgnoreCaseAndProductCategory(@Param("name") String name, @Param("category") String category);

    @NotNull Optional<Product> findById(@NotNull UUID productId);
    @NotNull List<Product> findAll();

    @Query("SELECT p.id FROM Product p")
    List<UUID> findAllIds();
}