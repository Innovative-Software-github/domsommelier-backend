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
    public enum Content {Brut_Nature, Extra_Brut}

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Content name;

    @OneToMany(mappedBy = "content")
    private List<SparklingWine> sparklingWines;
}
