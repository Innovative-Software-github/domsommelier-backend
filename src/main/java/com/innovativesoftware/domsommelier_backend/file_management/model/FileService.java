package com.innovativesoftware.domsommelier_backend.file_management.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<MultipartFile> uploadFiles(MultipartFile[] files, String bucket);
    byte[] getFileBytes(String bucket, String fileName);
}
