package com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "gourmet_product")
@NoArgsConstructor
@AllArgsConstructor
public class Snack {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory", nullable = false)
    private SnackCategory subcategory; // Сыр, Хамон, Брезаола, Ассорти

    @ElementCollection
    @CollectionTable(name = "gourmet_pairing", joinColumns = @JoinColumn(name = "gourmet_id"))
    @Column(name = "pairing")
    private List<String> pairings; // ["для Красного вина", "для Белого вина"]

    @Column(name = "producer")
    private String producer; // Campari, Martini, Aperol
}