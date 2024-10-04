package com.innovativesoftware.domsommelier_backend.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
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
    private Integer production_year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color", nullable = false)
    private WineColor color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private WineType type;
}