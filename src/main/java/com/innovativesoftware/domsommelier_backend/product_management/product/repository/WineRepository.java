package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.WineProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WineRepository extends JpaRepository<Wine, UUID> {
    @Query("""
        select
         product.id as id,
         product.name as name,
         product.price as price,
         product.discount as discount,
         product.description as description,
         product.country.name as country_name,
         product.region.name as region_name,
         wine.color.name as color,
         wine.sugar as sugar,
         wine.volume as volume
         from Product product
         inner join Wine wine on product.id = wine.id
    """)
    List<WineProjection> findAllWines();

    @Query("""
        select product.id as id, product.name as name, product.price as price, product.discount as discount from Product product
         inner join Wine wine on product.id = wine.id
         where product.country.name = :country
    """)
    List<WineProjection> findWinesByCountry(String country);
}