package com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit;

import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "spirit_strength")
@NoArgsConstructor
public class SpiritStrength {

    public record Strength(Integer percent) {
        @Override
        public String toString() {
            return percent + "%";
        }
    }

    public SpiritStrength(String name) {
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
        Strength strength = new Strength(percent);
        this.name = strength.toString();
    }

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "strength")
    private List<Spirit> spirits;
}
