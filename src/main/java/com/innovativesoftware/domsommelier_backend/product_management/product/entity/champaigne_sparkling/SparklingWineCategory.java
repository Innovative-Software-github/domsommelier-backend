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

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "subcategory")
    private List<SparklingWine> sparklingWineList;
}
