package com.innovativesoftware.domsommelier_backend.filter_management.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterValueDtoResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private UUID filterId;
    private String filterName;
    private UUID optionId;
}
