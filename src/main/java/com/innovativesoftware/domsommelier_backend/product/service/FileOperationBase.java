package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.shared.interf.FileService;
import org.springframework.beans.factory.annotation.Autowired;

public class FileOperationBase {
    @Autowired
    protected FileService fileService;

    public byte[] getBytesFromFile(String bucket, String fileName) {
        return fileService.getFileBytes(bucket, fileName);
    }
}
