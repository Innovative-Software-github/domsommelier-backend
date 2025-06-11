package com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "sparkling_wine_color")
public class SparklingWineColor {
    public enum Color {WHITE, PINK}

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Color name;

    @OneToMany(mappedBy = "color")
    private List<SparklingWine> sparklingWines;

}
