package com.innovativesoftware.domsommelier_backend.filter_management.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterOptionDtoCreateRequest {
    private String value;
    private UUID filterId;
}
