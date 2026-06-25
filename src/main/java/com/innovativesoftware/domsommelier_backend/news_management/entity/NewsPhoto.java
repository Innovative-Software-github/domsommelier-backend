package com.innovativesoftware.domsommelier_backend.news_management.entity;

import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Обложка новости в MinIO. {@code name} хранит полный ключ объекта ({@code <newsId>/<file>}),
 * чтобы чтение и удаление в MinIO были согласованы с загрузкой; {@code url} — готовая
 * публичная ссылка для отображения.
 */
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "news_photo")
public class NewsPhoto extends File {

    @OneToOne
    @JoinColumn(name = "news_id", nullable = false, unique = true)
    private News news;
}
