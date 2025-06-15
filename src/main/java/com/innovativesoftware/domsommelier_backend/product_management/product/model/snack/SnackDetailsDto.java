package com.innovativesoftware.domsommelier_backend.product_management.product.model.snack;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SnackDetailsDto {
    private String category;
    private List<String> pairings;
    private String producer;
}
