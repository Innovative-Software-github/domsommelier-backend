package com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "sparkling_wine")
@NoArgsConstructor
@AllArgsConstructor
public class SparklingWine {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory", nullable = false)
    private SparklingWineCategory category; // Шампанское, Игристое, Просекко

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sugar_content", nullable = false)
    private SugarContent content; // Brut Nature, Extra Brut и т.д.

    @Column(name = "producer")
    private String producer; // Moët & Chandon, Perrier-Jouët и т.д.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color", nullable = false)
    private SparklingWineColor color; // Белое, Розовое

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volume", nullable = false)
    private WineVolume volume; // 0.5 л, 0.7 л и т.д.

    @ElementCollection
    @CollectionTable(name = "sparkling_wine_feature", joinColumns = @JoinColumn(name = "sparkling_wine_id"))
    @Column(name = "feature")
    private List<String> features; // ["gift_set", "vintage", "limited"]
}