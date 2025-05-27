package com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "wine")
public class Wine {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Product product;

    @Column(name = "production_year", nullable = false)
    private Integer productionYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color", nullable = false)
    private WineColor color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private WineType type;
}