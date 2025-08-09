package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface SpiritRepository extends JpaRepository<Spirit, UUID>, JpaSpecificationExecutor<Spirit> {
    @Query("SELECT DISTINCT s.category.name FROM Spirit s")
    Set<String> getDistinctSubcategory();

    @Query("SELECT DISTINCT cast(s.strength as string) FROM Spirit s")
    Set<String> getDistinctStrength();

    @Query("SELECT DISTINCT s.producer FROM Spirit s")
    Set<String> getDistinctProducer();

    @Query("SELECT DISTINCT cast(s.volume as string) FROM Spirit s")
    Set<String> getDistinctVolume();

    @Query("SELECT DISTINCT f FROM Spirit s JOIN s.features f")
    Set<String> getDistinctFeature();

    @Query("SELECT DISTINCT s.product.productCountry.name FROM Spirit s")
    Set<String> findDistinctCountryNames();
}
