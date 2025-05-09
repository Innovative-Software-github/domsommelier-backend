package com.innovativesoftware.domsommelier_backend.filter_management.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterValueDtoRequest {

    @Schema(description = "можно не указывать при создании")
    private UUID id;
    private UUID productId;
    private UUID filterId;
}
