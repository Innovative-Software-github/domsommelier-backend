package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories.Accessories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface AccessoriesRepository extends JpaRepository<Accessories, UUID>, JpaSpecificationExecutor<Accessories> {
    @Query("SELECT DISTINCT a.product.productCountry.name FROM Accessories a")
    Set<String> findDistinctCountries();

    @Query("SELECT DISTINCT a.producer FROM Accessories a")
    Set<String> findDistinctProducers();

    @Query("SELECT DISTINCT a.features FROM Accessories a")
    Set<String> findDistinctFeatures();
}
