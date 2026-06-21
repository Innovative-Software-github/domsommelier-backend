package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {

    @Schema(description = "Поисковый запрос (название или артикул)", example = "Louis")
    private String q;

    @Schema(description = "Город (slug винотеки) — только товары в наличии", example = "moscow")
    private String city;

    @Schema(description = "Номер страницы (с 0)", example = "0")
    private Integer page = 0;

    @Schema(description = "Размер страницы", example = "20")
    private Integer size = 20;
}
