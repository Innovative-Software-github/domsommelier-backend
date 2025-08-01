package com.innovativesoftware.domsommelier_backend.news_management.service;

import com.innovativesoftware.domsommelier_backend.file_management.service.FileOperationService;
import com.innovativesoftware.domsommelier_backend.news_management.entity.News;
import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import com.innovativesoftware.domsommelier_backend.news_management.repository.NewsPhotoRepository;
import com.innovativesoftware.domsommelier_backend.news_management.repository.NewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NewsPhotoOperationService extends FileOperationService {
    @Autowired
    private NewsPhotoRepository newsPhotoRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Transactional
    public void uploadFilesWithRef(MultipartFile[] files, String bucket, String eventId) {
        List<MultipartFile> uploadedFiles = fileService.uploadFiles(files, eventId, bucket);
        News news = newsRepository.getReferenceById(UUID.fromString(eventId));

        List<NewsPhoto> readyFiles = (List<NewsPhoto>) uploadedFiles.stream()
                .map(uploadedFile -> {
                    try {
                        return NewsPhoto.builder()
                                .name(uploadedFile.getOriginalFilename())
                                .bucket(bucket)
                                .aNews(news)
                                .url(fileUrl(bucket, eventId, uploadedFile.getOriginalFilename()))
                                .build();
                    }
                    catch(Exception e) {
                        throw new RuntimeException("Problem with uploading news photos");
                    }
                })
                .toList();
        newsPhotoRepository.saveAll(readyFiles);
    }
}
