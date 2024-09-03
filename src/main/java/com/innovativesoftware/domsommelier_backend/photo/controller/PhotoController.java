package com.innovativesoftware.domsommelier_backend.photo.controller;

import com.innovativesoftware.domsommelier_backend.photo.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    private FileService fileService;

    @PostMapping("load-bucket")
    public void uploadPhoto(@RequestBody MultipartFile file, @RequestParam String bucket) {
        fileService.uploadFileToBucket(file, bucket);
    }
}

