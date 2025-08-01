package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.file_management.service.FileOperationService;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductPhoto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductPhotoOperationService extends FileOperationService {
    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void uploadFilesWithRef(MultipartFile[] files, String bucket, String productId) {
        List<MultipartFile> uploadedFiles = fileService.uploadFiles(files, productId, bucket);
        Product product = productRepository.getReferenceById(UUID.fromString(productId));

        List<ProductPhoto> readyFiles = (List<ProductPhoto>) uploadedFiles.stream()
                .map(uploadedFile -> {
                    try {
                        return ProductPhoto.builder()
                                .name(uploadedFile.getOriginalFilename())
                                .bucket(bucket)
                                .product(product)
                                .url(fileUrl(bucket, productId, uploadedFile.getOriginalFilename()))
                                .build();
                    }
                    catch(Exception e) {
                        throw new RuntimeException("Problem with uploading product photos");
                    }
                })
                .toList();
        productPhotoRepository.saveAll(readyFiles);
    }
}
