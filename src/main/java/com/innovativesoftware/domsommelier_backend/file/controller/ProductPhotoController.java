package com.innovativesoftware.domsommelier_backend.file.controller;

import com.innovativesoftware.domsommelier_backend.file.service.ProductPhotoOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/file/product-photo")
public class ProductPhotoController {

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

