package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product.entity.ProductPhoto;
import com.innovativesoftware.domsommelier_backend.shared.service.FileOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductPhotoOperationService extends FileOperationService<Product, ProductPhoto> {
    @Autowired
    public ProductPhotoOperationService(JpaRepository<ProductPhoto, String> fileRepository,
                                        JpaRepository<Product, UUID> fileDomainRepository) {
        super(fileRepository, fileDomainRepository);
    }
}
