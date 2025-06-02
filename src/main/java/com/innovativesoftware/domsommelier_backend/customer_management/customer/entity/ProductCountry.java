package com.innovativesoftware.domsommelier_backend.customer_management.customer.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_country")
public class ProductCountry {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "productCountry")
    private List<Product> products;
}