package com.innovativesoftware.domsommelier_backend.news_management.service;

import com.innovativesoftware.domsommelier_backend.news_management.entity.News;
import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsDto;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsRequest;
import com.innovativesoftware.domsommelier_backend.news_management.repository.NewsRepository;
import com.innovativesoftware.domsommelier_backend.news_management.util.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsPhotoOperationService coverService;

    @Transactional(readOnly = true)
    public Page<NewsDto> getNews(Pageable pageable) {
        return newsRepository.findAllByOrderByPublishedAtDesc(pageable).map(NewsMapper::toDto);
    }

    @Transactional(readOnly = true)
    public NewsDto getById(UUID id) {
        return NewsMapper.toDto(getOrThrow(id));
    }

    @Transactional
    public NewsDto create(NewsRequest request) {
        News news = News.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .reference(request.getReference())
                .publishedAt(OffsetDateTime.now())
                .build();
        return NewsMapper.toDto(newsRepository.save(news));
    }

    @Transactional
    public NewsDto update(UUID id, NewsRequest request) {
        News news = getOrThrow(id);
        news.setTitle(request.getTitle().trim());
        news.setDescription(request.getDescription());
        news.setReference(request.getReference());
        return NewsMapper.toDto(newsRepository.save(news));
    }

    @Transactional
    public void delete(UUID id) {
        News news = getOrThrow(id);
        coverService.deleteCover(id);
        newsRepository.delete(news);
    }

    @Transactional
    public NewsDto setCover(UUID id, MultipartFile file) {
        News news = getOrThrow(id);
        NewsPhoto cover = coverService.setCover(news, file);
        return NewsMapper.toDto(news, cover);
    }

    @Transactional
    public NewsDto removeCover(UUID id) {
        News news = getOrThrow(id);
        coverService.deleteCover(id);
        return NewsMapper.toDto(news, null);
    }

    /** Байты обложки для отдачи браузеру. {@link Optional#empty()} — обложки нет. */
    @Transactional(readOnly = true)
    public Optional<NewsPhotoOperationService.NewsCover> getCover(UUID id) {
        return coverService.getCover(id);
    }

    private News getOrThrow(UUID id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Новость не найдена: " + id));
    }
}
