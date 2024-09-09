package com.innovativesoftware.domsommelier_backend.file.interf;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    List<String> uploadFiles(MultipartFile[] files, String bucket);
    byte[] getFileBytes(String bucket, String fileName);
}
