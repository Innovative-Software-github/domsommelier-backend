package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "initial_price", nullable = false)
    private Integer initialPrice;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "description")
    private String description;

    @Column(name = "discount")
    private Integer discount;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private ProductCountry productCountry;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "product")
    private List<ProductPhoto> productPhoto;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

}