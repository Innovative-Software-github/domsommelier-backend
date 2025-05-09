package com.innovativesoftware.domsommelier_backend.order_management.order.model;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderItemDto {
    private UUID id;
    private Integer quantity;
    private UUID order;
}
