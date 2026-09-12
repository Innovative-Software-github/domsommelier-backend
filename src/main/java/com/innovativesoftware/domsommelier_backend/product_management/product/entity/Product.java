package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.ProductCountry;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderItem;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.ProductStock;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.StorageHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "brand", columnDefinition = "jsonb")
    private com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.Reference brand;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "packaging", columnDefinition = "jsonb")
    private com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.Packaging packaging;

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "article", nullable = false)
    private String article;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "initial_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal initialPrice;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "description")
    private String description;

    @Column(name = "aroma", columnDefinition = "TEXT")
    private String aroma;

    @Column(name = "taste", columnDefinition = "TEXT")
    private String taste;

    @Column(name = "food_pairing", columnDefinition = "TEXT")
    private String foodPairing;

    /**
     * Акционная цена товара для всех покупателей (не процент!). {@code null} — акции нет.
     * Эффективная цена = {@code salePrice != null ? salePrice : price}, см. {@code PriceCalculator}.
     */
    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_name", nullable = false)
    private ProductCountry productCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_name", nullable = false)
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "product")
    private List<ProductPhoto> productPhoto = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<StorageHistory> products = new ArrayList<>();

    /** Остатки товара по винотекам — основа доступности по городам (вариант А). */
    @OneToMany(mappedBy = "product")
    private List<ProductStock> stocks = new ArrayList<>();
}