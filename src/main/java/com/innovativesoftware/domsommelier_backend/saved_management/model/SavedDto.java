package com.innovativesoftware.domsommelier_backend.saved_management.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedDto implements Serializable {
    private UUID customerId;

    @Singular
    private List<UUID> items;
}
