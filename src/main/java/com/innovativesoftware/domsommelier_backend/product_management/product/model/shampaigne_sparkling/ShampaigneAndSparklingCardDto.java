package com.innovativesoftware.domsommelier_backend.product_management.product.model.shampaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class ShampaigneAndSparklingCardDto extends ProductCardDto {

    public enum Category { Champagne, Sparkling_wine, Prosecco }
    public enum Content {Brut_Nature, Extra_Brut}
    public enum Color {WHITE, PINK}
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
    private Content content;
    private Color color;
    private Volume volume;
}
