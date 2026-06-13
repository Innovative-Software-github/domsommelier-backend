package com.innovativesoftware.domsommelier_backend.product_management.store.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WineStoreRequestDto {
    @NotBlank
    private String name;

    private String address;

    private String phone;

    private String workingHours;

    @NotBlank
    private String city;

    @NotBlank
    private String district;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;
}
