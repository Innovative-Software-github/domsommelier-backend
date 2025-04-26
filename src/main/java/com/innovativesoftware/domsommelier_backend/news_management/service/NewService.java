package com.innovativesoftware.domsommelier_backend.news_management.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.news_management.entity.News;
import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileListDTO;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileProjection;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFilesRequest;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithoutFilesRequest;
import com.innovativesoftware.domsommelier_backend.news_management.repository.NewsPhotoRepository;
import com.innovativesoftware.domsommelier_backend.news_management.repository.NewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NewService {

    @Autowired
    private final NewsRepository newsRepository;
    @Autowired
    private final NewsPhotoRepository newsPhotoRepository;

    public List<NewsWithFileListDTO> findAllNews() {
        List<NewsWithFileProjection> newsWithFile = newsRepository.findAllNews();
        return newsWithFile.stream().map(newWithFile -> findNewsById(newWithFile.getId())).toList();
    }

    public UUID addNews(NewsWithoutFilesRequest newsWithoutFilesRequest) {
        var news = News.builder()
                .title(newsWithoutFilesRequest.getTitle())
                .description(newsWithoutFilesRequest.getDescription())
                .reference(newsWithoutFilesRequest.getReference())
                .build();
        newsRepository.save(news);
        return news.getId();
    }

    public NewsWithFileListDTO findNewsById(UUID id) {
        //return newsRepository.findNewsById(id);
        NewsWithFileProjection news = newsRepository.findNewsById(id);
        return NewsWithFileListDTO.builder()
                .id(news.getId())
                .description(news.getDescription())
                .title(news.getTitle())
                .reference(news.getReference())
                .files(
                        newsPhotoRepository.findFilesByNewsId(news.getId()).stream().map(newFile ->
                                FileDTO.builder()
                                        .id(newFile.getId())
                                        .bucket(newFile.getBucket())
                                        .name(newFile.getName())
                                        .description(newFile.getDescription())
                                        .build()
                        ).toList()
                )
                .build();
    }


    public void deleteNewsById(String id) {
        newsRepository.deleteNewsById(UUID.fromString(id));
    }

    @Transactional
    public void updateNews(NewsWithFilesRequest newsWithFilesRequest) {
        News news;
        try {
            news = newsRepository.getReferenceById(UUID.fromString(newsWithFilesRequest.getId()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Not found");
        }

        newsRepository.updateNews(
                UUID.fromString(newsWithFilesRequest.getId()),
                newsWithFilesRequest.getDescription(),
                newsWithFilesRequest.getTitle(),
                newsWithFilesRequest.getReference()
        );

        //var news = newsRepository.getReferenceById(UUID.fromString(newsWithFilesRequest.getId()));

        newsPhotoRepository.deleteFilesByNewsId(UUID.fromString(newsWithFilesRequest.getId()));
        for (var file : newsWithFilesRequest.getFiles()) {
            var newsPhoto = NewsPhoto.builder()
                    .bucket(file.getBucket())
                    .name(file.getName())
                    .description(file.getDescription())
                    .aNews(news)
                    .build();
            newsPhotoRepository.save(newsPhoto);
        }
    }
}
