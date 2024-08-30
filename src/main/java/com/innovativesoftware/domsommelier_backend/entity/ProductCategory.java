package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_category")
public class ProductCategory {

    // TODO: to enum
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "productCategory")
    private List<Product> products;
}