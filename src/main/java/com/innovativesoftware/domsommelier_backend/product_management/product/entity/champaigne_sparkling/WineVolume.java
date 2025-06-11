package com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "wine_volume")
public class WineVolume {
    @Getter
    @AllArgsConstructor
    public enum Volume {
        P02(0.2),
        P03(0.3),
        P05(0.5),
        P07(0.7),
        P1(1);

        private final double volume;

        @Override
        public String toString() {
            return volume + " л";
        }
    }

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Volume name;

    @OneToMany(mappedBy = "volume")
    private List<SparklingWine> sparklingWines;
}
