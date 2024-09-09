package com.innovativesoftware.domsommelier_backend.shared.service;

import com.innovativesoftware.domsommelier_backend.shared.interf.FileService;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Primary
@RequiredArgsConstructor
@Service
public class MinioService implements FileService {

    @Autowired
    private MinioClient minioClient;

    public List<String> uploadFiles(MultipartFile[] files, String bucketName) {
        List<String> fileLinks = new ArrayList<>();
        for (var file : files) {
            fileLinks.add(uploadOneFile(file, bucketName));
        }
        return fileLinks;
    }

    public byte[] getFileBytes(String bucket, String fileName) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fileName)
                        .build())) {
            return stream.readAllBytes();
        } catch (ServerException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    private String uploadOneFile(MultipartFile file, String bucketName) {
        try (InputStream stream = file.getInputStream()) {
            buildBucket(bucketName);
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(file.getOriginalFilename()).stream(
                                    stream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return bucketName + "/" + file.getOriginalFilename();
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
