package com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "accessories_product")
@NoArgsConstructor
@AllArgsConstructor
public class Accessories {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private Product product;

    @Column(name = "producer")
    private String producer;

    @ElementCollection
    @CollectionTable(name = "accessories_feature", joinColumns = @JoinColumn(name = "accessories_product_id"))
    @Column(name = "feature")
    private List<String> features;
}