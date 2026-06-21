package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.infrastructure.BucketRegistry;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductPhotoOperationService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@Hidden
@RequestMapping("/products/files")
public class ProductPhotoController {

    private final String BUCKET = BucketRegistry.Bucket.PRODUCT.getName();

    @Autowired
    private ProductPhotoOperationService fileOperationService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresAdmin
    public ResponseEntity<Void> uploadPhoto(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam String productId
    ) {
        fileOperationService.uploadFilesWithRef(files, BUCKET, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{photoId}")
    @RequiresAdmin
    public ResponseEntity<Void> deletePhoto(@PathVariable UUID photoId) {
        fileOperationService.deletePhotoById(photoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public byte[] downloadPhoto(@RequestParam("file") String fileName) {
        return fileOperationService.getBytesFromFile(BUCKET, fileName);
    }
}
