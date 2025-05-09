package com.innovativesoftware.domsommelier_backend.filter_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "filter_option")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterOption {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String value; // Значение ("Красное", "Белое")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id")
    private Filter filter;
}
