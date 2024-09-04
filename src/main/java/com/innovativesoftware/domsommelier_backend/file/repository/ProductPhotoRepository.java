package com.innovativesoftware.domsommelier_backend.file.repository;


import com.innovativesoftware.domsommelier_backend.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, String> {
}