package com.innovativesoftware.domsommelier_backend.news_management.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileListDTO;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileProjection;
import com.innovativesoftware.domsommelier_backend.news_management.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NewService {
    @Autowired
    private final NewsRepository newsRepository;

    public List<NewsWithFileListDTO> findAllNews() {
        List<NewsWithFileProjection> newsWithFile = newsRepository.findAllNews();
        return newsWithFile.stream().map(newWithFile ->
                new NewsWithFileListDTO()
                    .setId(newWithFile.getId())
                    .setDescription(newWithFile.getDescription())
                    .setTitle(newWithFile.getTitle())
                        .setFiles(
                                new ArrayList<>(List.of(
                                        new FileDTO()
                                                .setId(newWithFile.getNewsFileId())
                                                .setName(newWithFile.getFileName())
                                                .setBucket(newWithFile.getBucket())
                                                .setDescription(newWithFile.getNewFileDescription())
                                        )
                                )
                        )
                ).toList();
    }
}
