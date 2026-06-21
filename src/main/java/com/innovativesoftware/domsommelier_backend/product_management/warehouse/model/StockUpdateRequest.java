package com.innovativesoftware.domsommelier_backend.product_management.warehouse.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateRequest {

    @NotNull
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer quantity;
}
