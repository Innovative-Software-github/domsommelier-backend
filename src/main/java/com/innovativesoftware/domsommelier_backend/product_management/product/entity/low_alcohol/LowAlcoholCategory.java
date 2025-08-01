package com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "low_alcohol_category")
public class LowAlcoholCategory {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<LowAlcohol> snacks;
}
