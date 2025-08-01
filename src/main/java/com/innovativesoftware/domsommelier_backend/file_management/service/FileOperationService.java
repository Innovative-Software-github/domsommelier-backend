package com.innovativesoftware.domsommelier_backend.file_management.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileOperationService {
    @Autowired
    protected FileService fileService;

    public byte[] getBytesFromFile(String bucket, String fileName) {
        return fileService.getFileBytes(bucket, fileName);
    }

    public String fileUrl(String bucket, String path, String fileName) {
        return fileService.fileUrl(bucket, path, fileName);
    }
}
