package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.Snack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface SnackRepository extends JpaRepository<Snack, UUID>, JpaSpecificationExecutor<Snack> {

    @Query("SELECT DISTINCT s.category.name FROM Snack s")
    Set<String> findDistinctCategories();

    @Query("SELECT DISTINCT s.producer FROM Snack s")
    Set<String> findDistinctProducers();

    @Query("SELECT DISTINCT pairing FROM Snack s JOIN s.pairings pairing")
    Set<String> findDistinctPairings();

    @Query("SELECT DISTINCT s.product.productCountry.name FROM Snack s")
    Set<String> findDistinctCountryNames();
}
