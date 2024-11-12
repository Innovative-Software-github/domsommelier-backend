package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "wine_type")
public class WineType {
    enum Type { SWEET, SEMISWEET, DRY }

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Type name;

    @OneToMany(mappedBy = "type")
    private List<Wine> wines;
}