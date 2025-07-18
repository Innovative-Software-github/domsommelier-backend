package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.wine.WineProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WineRepository extends JpaRepository<Wine, UUID>, JpaSpecificationExecutor<Wine> {
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

    @Query("SELECT DISTINCT w.color.name FROM Wine w")
    Set<String> findDistinctColors();

    @Query("SELECT DISTINCT w.type.name FROM Wine w")
    Set<String> findDistinctTypes();

    @Query("SELECT DISTINCT g FROM Wine w JOIN w.grapes g")
    Set<String> findDistinctGrapes();

    @Query("SELECT DISTINCT f FROM Wine w JOIN w.features f")
    Set<String> findDistinctFeatures();

    @Query("SELECT DISTINCT w.producer FROM Wine w")
    Set<String> findDistinctProducers();

    @Query("SELECT DISTINCT cast(w.volume as string) FROM Wine w")
    Set<String> findDistinctVolumes();
}
