package com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory", nullable = false)
    private SparklingWineCategory subcategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sugar_content", nullable = false)
    private SugarContent sugarContent;

    @Column(name = "producer")
    private String producer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color", nullable = false)
    private SparklingWineColor color;

    @Column(name = "volume", nullable = false, precision = 4, scale = 2)
    private BigDecimal volume;

    @ElementCollection
    @CollectionTable(name = "sparkling_wine_feature", joinColumns = @JoinColumn(name = "sparkling_wine_id"))
    @Column(name = "feature")
    private List<String> features;
}