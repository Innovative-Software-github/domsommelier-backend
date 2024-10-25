package com.innovativesoftware.domsommelier_backend.product.repository;

import com.innovativesoftware.domsommelier_backend.product.entity.Wine;
import com.innovativesoftware.domsommelier_backend.product.model.ProductPhotoProjection;
import com.innovativesoftware.domsommelier_backend.product.model.WineDTO;
import com.innovativesoftware.domsommelier_backend.product.model.WineProjection;
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
}