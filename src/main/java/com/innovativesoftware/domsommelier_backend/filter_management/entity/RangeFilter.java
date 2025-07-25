package com.innovativesoftware.domsommelier_backend.filter_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "range_filter")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RangeFilter {
    @Id
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Filter filter;

    @Column
    private Double min;

    @Column
    private Double max;

    @Column
    private String unit;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private Double min;
        private Double max;
        private String label;
    }

    @ElementCollection
    @CollectionTable(
            name = "range_filter_step",
            joinColumns = @JoinColumn(name = "range_filter_id")
    )
    private List<Step> steps;
}
