package com.innovativesoftware.domsommelier_backend.filter_management.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterValueDtoCreateRequest {
    private UUID productId;
    private UUID filterId;
    private UUID filterOptionId; // для LIST
    private String value;        // для RANGE
}
