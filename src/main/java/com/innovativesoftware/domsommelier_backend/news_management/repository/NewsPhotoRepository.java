package com.innovativesoftware.domsommelier_backend.news_management.repository;

import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NewsPhotoRepository extends JpaRepository<NewsPhoto, UUID> {

    /** Обложка конкретной новости (одна на новость). */
    Optional<NewsPhoto> findByNews_Id(UUID newsId);
}
