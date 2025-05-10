package com.innovativesoftware.domsommelier_backend.filter_management.entity;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "filter")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filter {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String field;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategories productCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilterType type;

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilterOption> options;

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductFilterValue> productFilterValues;
}
