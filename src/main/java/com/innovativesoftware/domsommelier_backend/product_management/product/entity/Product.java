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
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
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

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_name", nullable = false)
    private ProductCountry productCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_name", nullable = false)
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "product")
    private List<ProductPhoto> productPhoto;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    private List<StorageHistory> products;

    /** Остатки товара по винотекам — основа доступности по городам (вариант А). */
    @OneToMany(mappedBy = "product")
    private List<ProductStock> stocks;
}