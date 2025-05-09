package com.innovativesoftware.domsommelier_backend.product_management.warehouse.model;


import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StorageHistoryDto {
    private UUID id;
    private Integer amount;
    private OffsetDateTime createdAt;
}
