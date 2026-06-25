package com.innovativesoftware.domsommelier_backend.news_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Ответ API новостей: используется и в списке, и в карточке/админ-форме. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {
    private UUID id;
    private String title;
    private String description;
    private String reference;
    /** Готовая публичная ссылка на обложку или {@code null}, если обложки нет. */
    private String coverUrl;
    private OffsetDateTime publishedAt;
}
