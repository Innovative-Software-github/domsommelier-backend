package com.innovativesoftware.domsommelier_backend.product_management.product.model.spirit;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class SpiritCardDto extends ProductCardDto {
    public enum Category { Whiskey, Cognac, Vodka, Tequila, Rum, }
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

    private Category category;
    private Strength strength;
    private Volume volume;
}
