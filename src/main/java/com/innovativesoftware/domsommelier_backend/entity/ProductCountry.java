package com.innovativesoftware.domsommelier_backend.entity;

import com.innovativesoftware.domsommelier_backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_country")
public class ProductCountry {
    // TODO: to enum
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "productCountry")
    private List<Product> products;
}