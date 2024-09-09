package com.innovativesoftware.domsommelier_backend.product.repository;


import com.innovativesoftware.domsommelier_backend.product.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, String> {
}