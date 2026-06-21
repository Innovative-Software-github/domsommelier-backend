package com.innovativesoftware.domsommelier_backend.product_management.product.model.write;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SparklingWriteRequest extends ProductWriteRequest {

    @NotBlank
    private String subcategory;

    @NotBlank
    private String sugarContent;

    @NotBlank
    private String color;

    private String producer;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal volume;

    private List<String> features;
}
