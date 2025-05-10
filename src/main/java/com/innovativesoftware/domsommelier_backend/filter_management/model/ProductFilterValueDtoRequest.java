package com.innovativesoftware.domsommelier_backend.filter_management.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterValueDtoRequest {

    private UUID id;
    private UUID productId;
    private UUID filterId;
    private UUID filterOptionId;
    private String value;
}
