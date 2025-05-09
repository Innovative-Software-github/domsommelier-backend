package com.innovativesoftware.domsommelier_backend.filter_management.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterOptionDtoResponse {
    private UUID id;
    private String value;
    private UUID filterId;
    private String filterName;
}
