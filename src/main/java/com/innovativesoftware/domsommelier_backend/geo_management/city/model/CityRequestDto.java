package com.innovativesoftware.domsommelier_backend.geo_management.city.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityRequestDto {

    /** Латинский slug в нижнем регистре: {@code moscow}, {@code saint-petersburg}. */
    @NotBlank
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "slug должен содержать только строчные латинские буквы, цифры и дефис")
    private String slug;

    @NotBlank
    private String name;

    /** Если не передано — город создаётся активным. */
    private Boolean active;

    /** Если не передано — 0. */
    private Integer sortOrder;
}
