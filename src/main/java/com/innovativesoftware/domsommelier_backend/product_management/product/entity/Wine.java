package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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

    @Column(name = "sugar")
    private Integer sugar;

    @Column(name = "volume")
    private Float volume;

    @ManyToMany
    @JoinTable(
    name = "wine_wine_sort",
    joinColumns = @JoinColumn(name = "wine_id"),
    inverseJoinColumns = @JoinColumn(name = "wine_sort_id"))
    private List<WineSort> wineSorts;
}