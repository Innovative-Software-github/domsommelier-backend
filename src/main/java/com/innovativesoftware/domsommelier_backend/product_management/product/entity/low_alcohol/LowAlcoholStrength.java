package com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "low_alcohol_strength")
public class LowAlcoholStrength {
    @Getter
    @AllArgsConstructor
    public enum Strength {
        P20(20),
        P30(30),
        P50(50),
        P70(70);

        private final int percent;

        @Override
        public String toString() {
            return percent + "%";
        }
    }

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Strength name;

    @OneToMany(mappedBy = "strength")
    private List<LowAlcohol> lowAlcoholList;
}
