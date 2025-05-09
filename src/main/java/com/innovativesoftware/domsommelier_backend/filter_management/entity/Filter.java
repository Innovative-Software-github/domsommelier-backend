package com.innovativesoftware.domsommelier_backend.filter_management.entity;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String field;

    @Column(nullable = false)
    private String productCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilterType type;

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilterOption> options;
}
