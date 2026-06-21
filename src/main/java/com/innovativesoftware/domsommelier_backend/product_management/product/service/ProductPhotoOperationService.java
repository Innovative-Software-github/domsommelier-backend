package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.file_management.service.FileOperationService;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductPhoto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    /** Удалить все фото товара: объекты в MinIO + строки в БД. */
    @Transactional
    public void deletePhotosByProductId(UUID productId) {
        List<ProductPhoto> photos = productPhotoRepository.findByProduct_Id(productId);
        for (ProductPhoto photo : photos) {
            fileService.deleteFile(photo.getBucket(), photo.getName());
            productPhotoRepository.delete(photo);
        }
    }

    /** Удалить одно фото товара по id. */
    @Transactional
    public void deletePhotoById(UUID photoId) {
        ProductPhoto photo = productPhotoRepository.findByPhotoId(photoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фото не найдено: " + photoId));
        fileService.deleteFile(photo.getBucket(), photo.getName());
        productPhotoRepository.delete(photo);
    }
}
