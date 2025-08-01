package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcohol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface LowAlcoholRepository extends JpaRepository<LowAlcohol, UUID>, JpaSpecificationExecutor<LowAlcohol> {
    @Query("SELECT DISTINCT l.category.name FROM LowAlcohol l")
    Set<String> findDistinctCategories();

    @Query("SELECT DISTINCT l.product.productCountry.name FROM LowAlcohol l")
    Set<String> findDistinctCountryNames();

    @Query("SELECT DISTINCT l.producer FROM LowAlcohol l")
    Set<String> findDistinctProducers();

    @Query("SELECT DISTINCT l.volume.name FROM LowAlcohol l")
    Set<String> findDistinctVolumes();
}
