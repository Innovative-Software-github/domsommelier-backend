package com.innovativesoftware.domsommelier_backend.shared.service;

import com.innovativesoftware.domsommelier_backend.shared.interf.FileService;
import com.innovativesoftware.domsommelier_backend.shared.model.File;
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
public class FileOperationService<TDomain, TFile extends File<TDomain>> {
    @Autowired
    protected FileService fileService;

    protected JpaRepository<TFile, String> fileRepository;

    protected JpaRepository<TDomain, UUID> fileDomainRepository;

    public FileOperationService(JpaRepository<TFile, String> fileRepository,
                                JpaRepository<TDomain, UUID> fileDomainRepository) {
        this.fileRepository = fileRepository;
        this.fileDomainRepository = fileDomainRepository;
    }

    public byte[] getBytesFromFile(String bucket, String fileName) {
        return fileService.getFileBytes(bucket, fileName);
    }

    @Transactional
    public void uploadFilesWithRef(MultipartFile[] files, String bucket, String fileBindingId, Class<TFile> fileType) {
        List<MultipartFile> uploadedFiles = fileService.uploadFiles(files, bucket);
        TDomain fileDomain = fileDomainRepository.getReferenceById(UUID.fromString(fileBindingId));


        List<TFile> readyFiles = uploadedFiles.stream()
                .map(uploadedFile -> {
                    try {
                        TFile file = fileType.getDeclaredConstructor().newInstance();
                        file.setName(uploadedFile.getOriginalFilename());
                        file.setBucket(bucket);
                        file.setDomain(fileDomain);
                        return file;
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        fileRepository.saveAll(readyFiles);
    }
}
