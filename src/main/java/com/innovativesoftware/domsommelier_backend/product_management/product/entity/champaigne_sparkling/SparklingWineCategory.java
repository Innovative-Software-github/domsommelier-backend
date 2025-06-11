package com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sparkling_wine_category")
public class SparklingWineCategory {

    public enum Category { Champagne, Sparkling_wine, Prosecco }

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Category name;

    @OneToMany(mappedBy = "category")
    private List<SparklingWine> sparklingWineList;
}
