package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.WineProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WineRepository extends JpaRepository<Wine, UUID> {
    @Query("""
        select product.id as id, product.name as name, product.price as price, product.discount as discount from Product product
         inner join Wine wine on product.id = wine.id
    """)
    List<WineProjection> findAllWines();

    @Query("""
        select product.id as id, product.name as name, product.price as price, product.discount as discount from Product product
         inner join Wine wine on product.id = wine.id
         where product.productCountry.name = :country
    """)
    List<WineProjection> findWinesByCountry(String country);
}