package com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "low_alcohol")
@NoArgsConstructor
@AllArgsConstructor
public class LowAlcohol {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory", nullable = false)
    private LowAlcoholCategory category; // Вермут, Аперитивы, Настойки

    @Column(name = "producer")
    private String producer; // Campari, Martini, Aperol

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volume", nullable = false)
    private LowAlcoholVolume volume; // 0.5 л, 0.7 л, 1 л, Другой


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strength", nullable = false)
    private LowAlcoholStrength strength; // до 10, 15%, 20%, 37% и т.д.

    @ElementCollection
    @CollectionTable(name = "low_alcohol_feature", joinColumns = @JoinColumn(name = "low_alcohol_id"))
    @Column(name = "feature")
    private List<String> features; // ["gift_set", "limited_edition"]
}