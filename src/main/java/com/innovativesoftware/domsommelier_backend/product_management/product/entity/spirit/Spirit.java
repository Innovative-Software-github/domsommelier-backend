package com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit;

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
@Table(name = "spirit")
@NoArgsConstructor
@AllArgsConstructor
public class Spirit {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory", nullable = false)
    private SpiritCategory category; // Виски, Коньяк, Водка и т.д.

    @Column(name = "strength", nullable = false, precision = 4, scale = 1)
    private BigDecimal strength;

    @Column(name = "producer")
    private String producer;

    @Column(name = "volume", nullable = false, precision = 4, scale = 2)
    private BigDecimal volume;

    @ElementCollection
    @CollectionTable(name = "spirit_feature", joinColumns = @JoinColumn(name = "spirit_id"))
    @Column(name = "feature")
    private List<String> features;
}