package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface SparklingWineRepository extends JpaRepository<SparklingWine, UUID>, JpaSpecificationExecutor<SparklingWine> {
    @Query("SELECT DISTINCT w.category.name FROM SparklingWine w")
    Set<String> findDistinctCategories();

    @Query("SELECT DISTINCT w.product.productCountry.name FROM SparklingWine w")
    Set<String> findDistinctCountryNames();

    @Query("SELECT DISTINCT w.content.name FROM SparklingWine w")
    Set<String> findDistinctSugarContents();

    @Query("SELECT DISTINCT w.producer FROM SparklingWine w")
    Set<String> findDistinctProducers();

    @Query("SELECT DISTINCT w.color.name FROM SparklingWine w")
    Set<String> findDistinctColors();

    @Query("SELECT DISTINCT s.volume.name FROM SparklingWine s")
    Set<String> findDistinctVolumes();

    @Query("SELECT DISTINCT f FROM SparklingWine s JOIN s.features f")
    Set<String> findDistinctFeatures();
}
