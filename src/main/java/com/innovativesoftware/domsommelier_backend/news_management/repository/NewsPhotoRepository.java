package com.innovativesoftware.domsommelier_backend.news_management.repository;

import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NewsPhotoRepository extends JpaRepository<NewsPhoto, UUID> {
}
