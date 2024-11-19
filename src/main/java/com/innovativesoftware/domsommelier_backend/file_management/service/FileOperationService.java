package com.innovativesoftware.domsommelier_backend.file_management.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileService;
import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileOperationService {
    @Autowired
    protected FileService fileService;

    public byte[] getBytesFromFile(String bucket, String fileName) {
        return fileService.getFileBytes(bucket, fileName);
    }
}
