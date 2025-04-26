package com.innovativesoftware.domsommelier_backend.news_management.repository;

import com.innovativesoftware.domsommelier_backend.news_management.entity.News;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<News, UUID> {
    @Query("""
        select 
            news.id as id,
            news.description as description,
            news.title as title,
            newsPhoto.id as newFileId,
            newsPhoto.bucket as bucket,
            newsPhoto.name as fileName,
            newsPhoto.description as newFileDescription
          from News news left join NewsPhoto newsPhoto on news.id = newsPhoto.aNews.id
    """)
    List<NewsWithFileProjection> findAllNews();
}
