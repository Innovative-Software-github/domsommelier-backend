package com.innovativesoftware.domsommelier_backend.filter_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "multi_select_filter")
public class MultiSelectFilter {

    @Id
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Filter filter;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        private String value;
        private String label;
    }

    @ElementCollection
    @CollectionTable(
            name = "multi_select_filter_option",
            joinColumns = @JoinColumn(name = "multi_select_filter_id")
    )
    private List<Option> options;
}
