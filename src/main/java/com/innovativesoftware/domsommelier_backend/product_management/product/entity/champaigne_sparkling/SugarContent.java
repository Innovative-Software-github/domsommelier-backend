package com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sugar_content")
public class SugarContent {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "sugarContent")
    private List<SparklingWine> sparklingWines;
}
