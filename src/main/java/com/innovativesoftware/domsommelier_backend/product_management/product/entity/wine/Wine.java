package com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine;

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
@Table(name = "wine")
@NoArgsConstructor
@AllArgsConstructor
public class Wine {
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "extended_details", columnDefinition = "jsonb")
    private com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.WineAttributes extendedDetails;

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    //@OneToOne
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

    @ElementCollection
    @CollectionTable(name = "wine_grape", joinColumns = @JoinColumn(name = "wine_id"))
    @Column(name = "grape")
    private List<String> grapes; // например: ["merlot", "chardonnay"]

    @Column(name = "producer")
    private String producer;

    @Column(name = "volume", nullable = false, precision = 4, scale = 2)
    private BigDecimal volume;

    @ElementCollection
    @CollectionTable(name = "wine_feature", joinColumns = @JoinColumn(name = "wine_id"))
    @Column(name = "feature")
    private List<String> features; // напр: ["gift_wrapping", "collection"]
}