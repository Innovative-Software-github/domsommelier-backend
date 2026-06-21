package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.WineColor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineColorRepository extends JpaRepository<WineColor, String> {
}
