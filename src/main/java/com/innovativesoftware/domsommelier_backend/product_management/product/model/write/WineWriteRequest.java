package com.innovativesoftware.domsommelier_backend.product_management.product.model.write;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class WineWriteRequest extends ProductWriteRequest {

    @NotNull
    @Min(1900)
    @Max(2100)
    private Integer productionYear;

    @NotBlank
    private String color;

    /** Тип вина опционален (в сущности {@code Wine.type} nullable). */
    private String type;

    private List<String> grapes;

    private String producer;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal volume;

    private List<String> features;
}
