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
    private LowAlcoholCategory category;

    @Column(name = "producer")
    private String producer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volume", nullable = false)
    private LowAlcoholVolume volume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strength", nullable = false)
    private LowAlcoholStrength strength;

    @ElementCollection
    @CollectionTable(name = "low_alcohol_feature", joinColumns = @JoinColumn(name = "low_alcohol_id"))
    @Column(name = "feature")
    private List<String> features;
}