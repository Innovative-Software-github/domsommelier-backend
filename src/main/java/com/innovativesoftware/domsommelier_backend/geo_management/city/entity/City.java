package com.innovativesoftware.domsommelier_backend.geo_management.city.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Справочник городов присутствия (Москва, Пермь, ...).
 * <p>
 * Единый источник правды для выбора города на витрине, валидации и фильтрации
 * ассортимента/мероприятий. {@code slug} — стабильный латинский ключ, по которому
 * город передаётся в API и хранится в {@code wine_store.city}.
 */
@Getter
@Setter
@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Стабильный латинский идентификатор: {@code moscow}, {@code perm}. Уникален. */
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    /** Человекочитаемое название: {@code Москва}, {@code Пермь}. */
    @Column(name = "name", nullable = false)
    private String name;

    /** Показывать ли город в выборе на витрине. */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /** Порядок отображения в списке (по возрастанию). */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}
