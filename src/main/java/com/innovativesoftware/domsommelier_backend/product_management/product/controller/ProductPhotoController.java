package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.infrastructure.BucketRegistry;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductPhotoOperationService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Hidden
@RequestMapping("/products/files")
public class ProductPhotoController {

    private final String BUCKET = BucketRegistry.Bucket.PRODUCT.getName();

    @Autowired
    private ProductPhotoOperationService fileOperationService;

    @PostMapping("upload")
    public void uploadPhoto(@RequestBody MultipartFile[] files, @RequestParam String productId) {
        fileOperationService.uploadFilesWithRef(files, BUCKET, productId);
    }

    @GetMapping("")
    public byte[] downloadPhoto(@RequestParam("file") String fileName) {
        return fileOperationService.getBytesFromFile(BUCKET, fileName);
    }
}

