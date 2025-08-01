package com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "wine_volume")
public class WineVolume {

    public record Volume(double volume) {
        @Override
        public String toString() {
            return volume + " л";
        }
    }

    public WineVolume(String name) {
        super();
        double vol;
        try {
            vol = Double.parseDouble(name
                    .replace("л", "")
                    .replace(" ", "")
                    .replace(",", ".")
                    .replace("l", "")
                    .trim()
            );
        } catch (NumberFormatException e) {
            throw new InvalidValueException(
                    "Неверное значение для объёма, должно быть числом: " + name,
                    "INVALID_TYPE",
                    e.getMessage()
            );
        }
        if (vol < 0) {
            throw new InvalidValueException(
                    "Неверное значение для объёма, должно быть больше нуля: " + name,
                    "INVALID_TYPE",
                    "Неверное значение для крепкости"
            );
        }
        Volume volume = new Volume(vol);
        this.name = volume.toString();
    }

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "volume")
    private List<SparklingWine> sparklingWines;
}
