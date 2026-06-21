package com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * Текущий остаток товара в конкретной винотеке.
 * <p>
 * Источник правды для доступности товара по городам (вариант А): товар считается
 * доступным в городе, если у него есть запись с {@code quantity > 0} в любой винотеке
 * этого города. Уникальность пары (product, wineStore) гарантирует одну строку остатка
 * на товар в точке.
 */
@Getter
@Setter
@Entity
@Table(
        name = "product_stock",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_product_stock_product_store",
                columnNames = {"product_id", "wine_store_id"}
        )
)
public class ProductStock {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wine_store_id", nullable = false)
    private WineStore wineStore;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
