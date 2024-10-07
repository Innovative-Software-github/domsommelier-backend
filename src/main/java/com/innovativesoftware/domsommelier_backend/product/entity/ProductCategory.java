package com.innovativesoftware.domsommelier_backend.product.entity;

import com.innovativesoftware.domsommelier_backend.product.enums.ProductCategories;
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
    private ProductCategories name;

    @OneToMany(mappedBy = "productCategory")
    private List<Product> products;
}