package com.innovativesoftware.domsommelier_backend.photo.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class FileService {

    @Autowired
    private MinioClient minioClient;

    public void uploadFileToBucket(MultipartFile file, String bucketName) {
        try (InputStream stream = file.getInputStream()) {
            buildBucket(bucketName);
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(file.getOriginalFilename()).stream(
                                    stream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void buildBucket(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
