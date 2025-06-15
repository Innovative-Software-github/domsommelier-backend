package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SparklingWineRepository extends JpaRepository<SparklingWine, UUID>, JpaSpecificationExecutor<SparklingWine> {
}
