package com.innovativesoftware.domsommelier_backend.product_management.product.model.write;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Базовый запрос на создание/обновление товара. Конкретный подтип выбирается
 * по полю-дискриминатору {@code category} (Jackson), что и валидируется через {@code @Valid}.
 * Новые категории добавляются как подклассы + запись в {@link JsonSubTypes}.
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WineWriteRequest.class, name = "wine"),
        @JsonSubTypes.Type(value = SpiritWriteRequest.class, name = "spirit"),
        @JsonSubTypes.Type(value = SparklingWriteRequest.class, name = "champagne_and_sparkling"),
        @JsonSubTypes.Type(value = LowAlcoholWriteRequest.class, name = "low_alcohol"),
        @JsonSubTypes.Type(value = SnackWriteRequest.class, name = "snack"),
        @JsonSubTypes.Type(value = AccessoriesWriteRequest.class, name = "accessories")
})
public abstract class ProductWriteRequest {

    @NotNull
    private ProductCategoryEnum category;

    @NotBlank
    private String article;

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal initialPrice;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String description;

    @Min(0)
    @Max(100)
    private Integer discount;

    @NotBlank
    private String country;
}
