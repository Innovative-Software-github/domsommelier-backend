package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AttributeReferenceRepository extends JpaRepository<AttributeReference, String> {
    List<AttributeReference> findByKindOrderByLabelAsc(String kind);
    boolean existsByKindAndLabelIgnoreCase(String kind, String label);
}
