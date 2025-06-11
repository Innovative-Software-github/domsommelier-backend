package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpiritRepository extends JpaRepository<Spirit, UUID>, JpaSpecificationExecutor<Spirit> {
}
