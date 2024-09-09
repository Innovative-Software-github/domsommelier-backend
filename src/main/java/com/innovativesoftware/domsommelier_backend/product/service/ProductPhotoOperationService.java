package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product.entity.ProductPhoto;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ProductPhotoOperationService extends FileOperationBase {
    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    @Autowired
    private ProductRepository productRepository;

    public void uploadFilesWithRef(MultipartFile[] files, String bucket, String productId) {
        List<String> photoLinks = fileService.uploadFiles(files, bucket);
        Product product = productRepository.getReferenceById(UUID.fromString(productId));

        List<ProductPhoto> photos = photoLinks.stream()
                .map(photoLink -> {
                    ProductPhoto productPhoto = new ProductPhoto();
                    productPhoto.setLink(photoLink);
                    productPhoto.setName(photoLink);
                    productPhoto.setProduct(product);
                    return productPhoto;
                })
                .toList();
        productPhotoRepository.saveAll(photos);
    }
}
