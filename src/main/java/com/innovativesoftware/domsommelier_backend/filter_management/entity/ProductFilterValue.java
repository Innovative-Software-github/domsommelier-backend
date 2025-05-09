package com.innovativesoftware.domsommelier_backend.filter_management.entity;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_filter_value")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterValue {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id")
    private Filter filter;
}
