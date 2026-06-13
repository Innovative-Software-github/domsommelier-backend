package com.innovativesoftware.domsommelier_backend.saved_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

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
    private List<SavedItemDto> items;
}
