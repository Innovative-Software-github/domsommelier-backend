package com.innovativesoftware.domsommelier_backend.news_management.repository;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NewsPhotoRepository extends JpaRepository<NewsPhoto, UUID> {

    @Query("""
        select
            new com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO(
                            newsPhoto.id, newsPhoto.bucket, newsPhoto.name, newsPhoto.description)
        from NewsPhoto newsPhoto
        where newsPhoto.aNews.id = :id
    """)
    List<FileDTO> findFilesByNewsId(UUID id);


    @Modifying
    @Transactional
    @Query("""
        delete
            from NewsPhoto newsPhoto
            where newsPhoto.aNews.id = :id
    """)
    void deleteFilesByNewsId(UUID id);
}
