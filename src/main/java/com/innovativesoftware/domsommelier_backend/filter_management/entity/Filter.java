package com.innovativesoftware.domsommelier_backend.filter_management.entity;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "filter")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filter {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String field;   // название поля на английском

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private ProductCategoryEnum productCategoryEnum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilterType type;

//    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<FilterOption> options;
//
//    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ProductFilterValue> productFilterValues;
}
