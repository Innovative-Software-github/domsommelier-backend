package com.innovativesoftware.domsommelier_backend.admin_management.customer.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCustomerDiscountRequest {

    /** Процент личной скидки, 0 — снять скидку. */
    @NotNull
    @Min(0)
    @Max(100)
    private Integer percent;

    /** Основание — зачем дали скидку. Видно только в админке. */
    @Size(max = 255)
    private String comment;
}
