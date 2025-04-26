package com.innovativesoftware.domsommelier_backend.news_management.repository;

import com.innovativesoftware.domsommelier_backend.news_management.entity.News;
import com.innovativesoftware.domsommelier_backend.news_management.model.NewsWithFileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<News, UUID> {
    @Query("""
        select 
            news.id as id,
            news.description as description,
            news.title as title,
            news.reference as reference,
            newsPhoto.id as newFileId,
            newsPhoto.bucket as bucket,
            newsPhoto.name as fileName,
            newsPhoto.description as newFileDescription
          from News news left join NewsPhoto newsPhoto on news.id = newsPhoto.aNews.id
    """)
    List<NewsWithFileProjection> findAllNews();

    @Query("""
        select
            news.id as id,
            news.description as description,
            news.title as title,
            news.reference as reference,
            newsPhoto.id as newFileId,
            newsPhoto.bucket as bucket,
            newsPhoto.name as fileName,
            newsPhoto.description as newFileDescription
            from News news
            left join NewsPhoto newsPhoto on news.id = newsPhoto.aNews.id
            where news.id = :id
    """)
    NewsWithFileProjection findNewsById(UUID id);


    @Modifying
    @Transactional
    @Query("""
        delete News where id = :id
    """)
    void deleteNewsById(UUID id);

    @Modifying
    @Transactional
    @Query("""
        update News set 
            reference = :reference, 
            description = :description, 
            title = :title 
        where id = :id
    """)
    void updateNews(UUID id, String description, String title, String reference);
}
