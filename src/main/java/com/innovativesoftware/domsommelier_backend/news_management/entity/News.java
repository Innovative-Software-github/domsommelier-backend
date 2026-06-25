package com.innovativesoftware.domsommelier_backend.news_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Новость витрины: заголовок, текст, опциональная ссылка-источник и обложка.
 * Список сортируется по {@link #publishedAt} (свежие сверху).
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 4000)
    private String description;

    /** Опциональная внешняя ссылка-источник. На витрине переходом не используется. */
    @Column(name = "reference")
    private String reference;

    @Column(name = "published_at", nullable = false)
    private OffsetDateTime publishedAt;

    /** Обложка (одна на новость). Удаляется явно в сервисе перед удалением новости. */
    @OneToOne(mappedBy = "news", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private NewsPhoto cover;
}
