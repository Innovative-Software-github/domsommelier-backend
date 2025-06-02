package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_category")
public class ProductCategory {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private ProductCategoryEnum name;

    @Column(name = "label", nullable = false)
    private String label;

    @JsonIgnore
    @OneToMany(mappedBy = "productCategory")
    private List<Product> products;
}