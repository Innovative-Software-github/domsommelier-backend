package com.innovativesoftware.domsommelier_backend.news_management.util;

import com.innovativesoftware.domsommelier_backend.news_management.entity.News;
import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsDto;

public final class NewsMapper {

    private NewsMapper() {
    }

    public static NewsDto toDto(News news) {
        return toDto(news, news.getCover());
    }

    /**
     * Вариант с явной обложкой — на случай, когда в той же транзакции обложка только что
     * изменена и {@code news.getCover()} ещё может быть неактуальной.
     */
    public static NewsDto toDto(News news, NewsPhoto cover) {
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .description(news.getDescription())
                .reference(news.getReference())
                .publishedAt(news.getPublishedAt())
                // Относительный путь: обложка отдаётся бэкендом (через /api-back на фронте),
                // а не прямой ссылкой на MinIO (внутренний хост, приватный бакет).
                .coverUrl(cover != null ? "/api/v1/news/" + news.getId() + "/cover" : null)
                .build();
    }
}
