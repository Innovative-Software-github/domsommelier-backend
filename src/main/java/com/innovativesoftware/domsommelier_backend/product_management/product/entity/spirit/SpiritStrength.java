package com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "spirit_strength")
public class SpiritStrength {
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
    private List<Spirit> spirits;
}
