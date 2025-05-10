package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.ProductFilterValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductFilterValueRepository extends JpaRepository<ProductFilterValue, UUID> {
    List<ProductFilterValue> findAllByProductId(UUID productId);


    List<ProductFilterValue> findAllByFilterIdAndOptionId(UUID filterId, UUID filterOptionId);
}
