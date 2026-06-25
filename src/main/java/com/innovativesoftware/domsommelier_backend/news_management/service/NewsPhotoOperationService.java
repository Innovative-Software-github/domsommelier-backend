package com.innovativesoftware.domsommelier_backend.news_management.service;

import com.innovativesoftware.domsommelier_backend.file_management.service.FileOperationService;
import com.innovativesoftware.domsommelier_backend.infrastructure.BucketRegistry;
import com.innovativesoftware.domsommelier_backend.news_management.entity.News;
import com.innovativesoftware.domsommelier_backend.news_management.entity.NewsPhoto;
import com.innovativesoftware.domsommelier_backend.news_management.repository.NewsPhotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * Управление обложкой новости (одна на новость).
 * <p>
 * Объект кладётся в MinIO под ключом {@code <newsId>/<file>}; этот же ключ хранится в
 * {@link NewsPhoto#getName()} — чтобы чтение и удаление в MinIO были согласованы с загрузкой.
 * Байты обложки отдаёт бэкенд (см. {@link #getCover}), поэтому прямой публичный URL не хранится.
 */
@Service
@RequiredArgsConstructor
public class NewsPhotoOperationService extends FileOperationService {

    private static final String BUCKET = BucketRegistry.Bucket.NEWS.getName();

    private final NewsPhotoRepository newsPhotoRepository;

    /** Загружает обложку, заменяя предыдущую (если была). */
    @Transactional
    public NewsPhoto setCover(News news, MultipartFile file) {
        deleteCover(news.getId());
        // гарантируем, что удаление прежней обложки уйдёт в БД раньше вставки новой
        // (уникальный constraint на news_id)
        newsPhotoRepository.flush();

        String original = file.getOriginalFilename();
        String fileName = (original == null || original.isBlank()) ? "cover" : original;
        String objectKey = news.getId() + "/" + fileName;

        fileService.uploadFiles(new MultipartFile[]{file}, news.getId().toString(), BUCKET);

        NewsPhoto cover = NewsPhoto.builder()
                .name(objectKey)
                .bucket(BUCKET)
                .news(news)
                .build();

        return newsPhotoRepository.save(cover);
    }

    /** Удаляет обложку новости из MinIO и БД (если есть). */
    @Transactional
    public void deleteCover(UUID newsId) {
        newsPhotoRepository.findByNews_Id(newsId).ifPresent(cover -> {
            fileService.deleteFile(cover.getBucket(), cover.getName());
            newsPhotoRepository.delete(cover);
        });
    }

    /** Имя файла и байты обложки для отдачи браузеру. {@link Optional#empty()} — обложки нет. */
    @Transactional
    public Optional<NewsCover> getCover(UUID newsId) {
        return newsPhotoRepository.findByNews_Id(newsId)
                .map(cover -> new NewsCover(cover.getName(), getBytesFromFile(cover.getBucket(), cover.getName())));
    }

    public record NewsCover(String fileName, byte[] bytes) {
    }
}
