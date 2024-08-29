package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

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