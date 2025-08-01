package com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol;

import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.SpiritStrength;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "low_alcohol_strength")
@NoArgsConstructor
public class LowAlcoholStrength {

    public record Strength(Integer percent) {
        @Override
        public String toString() {
            return percent + "%";
        }
    }

    public LowAlcoholStrength(String name) {
        super();
        int percent = 0;
        try {
            percent = Integer.parseInt(
                    name.replace("%", "").replace(" ", "").trim());
        } catch (NumberFormatException e) {
            throw new InvalidValueException(
                    "Неверное значение для крепкости, должно быть числом: " + name,
                    "INVALID_TYPE",
                    e.getMessage()
            );
        }
        if (percent < 0) {
            throw new InvalidValueException(
                    "Неверное значение для крепкости, должно быть больше нуля: " + name,
                    "INVALID_TYPE",
                    "Неверное значение для крепкости"
            );
        }
        SpiritStrength.Strength strength = new SpiritStrength.Strength(percent);
        this.name = strength.toString();
    }

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "strength")
    private List<LowAlcohol> lowAlcoholList;
}
