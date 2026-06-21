package com.innovativesoftware.domsommelier_backend.product_management.product.model.write;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SnackWriteRequest extends ProductWriteRequest {

    @NotBlank
    private String subcategory;

    private List<String> pairings;

    private String producer;
}
