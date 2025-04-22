package com.innovativesoftware.domsommelier_backend.product_management.product.controller;

import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductPhotoOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products/files")
public class ProductPhotoController {

    private static final Logger log = LoggerFactory.getLogger(ProductPhotoController.class);
    private final String BUCKET = "product";

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

