package com.innovativesoftware.domsommelier_backend;

import com.innovativesoftware.domsommelier_backend.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

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